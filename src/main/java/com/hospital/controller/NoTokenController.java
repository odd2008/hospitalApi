package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.constant.Constants;
import com.hospital.dao.UserDao;
import com.hospital.entity.User;
import com.hospital.tools.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/noToken")
public class NoTokenController {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.fromMail.addr}")
    private String from;
    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UserDao userDao;

    private final static Logger logger = LoggerFactory.getLogger(NoTokenController.class);

    @PostMapping(value="/login")
    public Map<String, Object> checkLogin(String telephone, String password) throws Exception{
        logger.info(telephone);
        Map<String, Object> resultMap = new HashMap<>();
        User user = userDao.getUserByTelephone(telephone);
        if (user != null){
            if (MD5EncodingHelper.EncodeByMD5(password + user.getPasswordSalt()).equals(user.getPasswordHash())){
                Map aesMap = new HashMap<String,String>();
                aesMap.put("telephone", telephone);
                aesMap.put("password", user.getPasswordHash());
                String content = JSONObject.toJSONString(aesMap);
                logger.info(content);
                String encrypt = AesEncryptHelper.encrypt(content, Constants.JWT_TOKEN_KEY);
                System.out.println("加密后：" + encrypt);
                String token= JwtTokenHelper.createToken(encrypt, true);
                resultMap.put("status", "success");
                resultMap.put("errMsg", "");
                resultMap.put("data", token);
            } else {
                resultMap.put("status", "error");
                resultMap.put("errMsg", "登录失败，密码不正确！");
                resultMap.put("data", "");
            }
        } else {
            resultMap.put("status", "error");
            resultMap.put("errMsg", "登录失败，账号不存在！");
            resultMap.put("data", "");
        }
        return resultMap;
    }

    @PostMapping(value="/register")
    public Map<String, Object> register(String telephone, String password, String mail, String code_type, String code) {
        Map<String, Object> resultMap = new HashMap<>();

        // 判断验证码是否正确
        String mailKey = getRedisKey(mail, code_type);
        if(!redisHelper.hasKey(mailKey)) {
            resultMap.put("status", "error");
            resultMap.put("errMsg", "验证码失效，请重新获取");
            resultMap.put("data", "");
            return resultMap;
        } else {
            if (redisHelper.get(mailKey).equals(code)){
                resultMap.put("status", "success");
                resultMap.put("errMsg", "");
                resultMap.put("data", JwtTokenHelper.createToken(mail, true));
            } else {
                resultMap.put("status", "error");
                resultMap.put("errMsg", "验证码有误，请重新输入");
                resultMap.put("data", "");
                return resultMap;
            }
        }
        //判断是否用户已存在
        User user = userDao.getUserByTelephone(telephone);
        if (user != null) {
            resultMap.put("status", "error");
            resultMap.put("errMsg", "用户已存在，请前往登录！");
            resultMap.put("data", "");
            return resultMap;
        }
        // 注册用户
        user = new User();
        user.setTelephone(telephone);
        //获取密码盐
        String salt= PassSaltHelper.GetSalt(16);
        user.setPasswordSalt(salt);
        //加盐加密
        user.setPasswordHash(MD5EncodingHelper.EncodeByMD5(password + salt));
        user.setMail(mail);
        userDao.addUser(user);
        return resultMap;
    }

    @PostMapping(value = "/getMailCode")
    public Map<String, Object> getMailCode(String mail, String code_type) {
        Map<String, Object> resultMap = new HashMap<>();
        logger.info(mail);
        String code= CommonHelper.GetVerifyCode(6);
        redisHelper.set(getRedisKey(mail, code_type) , code,60 * 5);//存入redis
        logger.info("sendmailcode已经运行");
        try {
            sendSimpleEmail(mail,"预约挂号验证码","【注册】您的手机验证码："+code+"，请尽快填写完成验证。");
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        resultMap.put("data", "验证码已发送到您的邮箱");
        return resultMap;
    }

    private String getRedisKey(String email, String type) {
        return email + "_" + type;
    }

    /**
     * 发送验证码
     * @param to
     * @param subject
     * @param content
     */
    private void sendSimpleEmail(String to, String subject, String content){
        MailHelper mailHelper=new MailHelper(mailSender);
        try {
            mailHelper.asyncSendMail(from,to,subject,content);
            logger.info("邮件已经异步发送");
        } catch (Exception e) {
            logger.info("邮件发送异常");
        }

    }
}
