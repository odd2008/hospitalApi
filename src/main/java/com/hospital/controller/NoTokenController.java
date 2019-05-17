package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.constant.Constants;
import com.hospital.dao.*;
import com.hospital.dto.DoctorDetailDTO;
import com.hospital.dto.HealthDocDTO;
import com.hospital.dto.UserDTO;
import com.hospital.entity.*;
import com.hospital.tools.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private DepartDao departDao;

    @Autowired
    private HealthDocDao healthDocDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private AppointOrderDao appointOrderDao;

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

    @GetMapping(value="/getAllDoctor", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllDoctor(@RequestParam("callback") String callback) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<DoctorDepart> doctors = doctorDao.getAllDoctor();

        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", doctors);
        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/getAllUser", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllUser(@RequestParam("callback") String callback) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<User> users = userDao.getAllUser();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User u:users) {
            userDTOS.add(new UserDTO(u, false));
        }
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", userDTOS);
        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/addAppointTime", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addAppointTime(AppointTime appointTime, @RequestParam("callback") String callback) throws Exception {

        doctorDao.addAppointTime(appointTime);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", "");

        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/delAppointTime", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String delAppointTime(Integer id, @RequestParam("callback") String callback) throws Exception {

        Map<String, Integer> param = new HashMap<>();
        param.put("id", id);
        doctorDao.delAppointTime(param);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", "");

        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/getAppointTime", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAppointTime(Integer doctorId, @RequestParam("callback") String callback) throws Exception {
        Map<String, Integer> param = new HashMap<>();
        param.put("doctorId", doctorId);
        List<AppointTime> appointTimes = doctorDao.getAppointTime(param);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", appointTimes);

        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/getAllComment", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllComment(Integer targetType, Integer targetId, @RequestParam("callback") String callback) throws Exception {
        Map<String, Integer> param = new HashMap<>();
        param.put("targetType", targetType);
        if(targetType == 2) {
            // 获取科室的id
            Map<String, Integer> doctorParam = new HashMap<>();
            doctorParam.put("id", targetId);
            Doctor doctor = doctorDao.getDoctorById(doctorParam);
            targetId = doctor.getDepartId();
        }

        param.put("targetId", targetId);
        List<Content> contents = contentDao.getComment(param);
        List<Map<String, Object>> resultList = new ArrayList<>();
        contents.stream().forEach(content -> {
            Map<String, Object> result = new HashMap<>();
            // 取信息
            User user = userDao.getUserByTelephone(content.getTelephone());
            UserDTO userDTO = new UserDTO(user, false);
            result.put("user", userDTO);
            result.put("comment", content);
            resultList.add(result);
        });

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", resultList);
        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/getAppoint", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAppoint(Integer doctorId, @RequestParam("callback") String callback) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Integer> param = new HashMap<>();
        param.put("doctorId", doctorId);
        List<AppointOrder> orders = appointOrderDao.getAppoint(param);
        List<Map<String, Object>> orderResults = new ArrayList<>();
        for (AppointOrder o:orders) {
            Map<String, Object> orderResult = new HashMap<>();
            orderResult.put("order", o);
            HealthDoc healthDoc = healthDocDao.getHealthDocByTelephone(o.getTelephone());
            orderResult.put("health", healthDoc);
            param.put("healthId", healthDoc.getId());
            List<HealthDocImage> healthDocImages = healthDocDao.getHealthDocImageByHealthId(param);
            orderResult.put("report",healthDocImages);
            orderResult.put("user", new UserDTO(userDao.getUserByTelephone(o.getTelephone()), false));
            Map<String, Integer> p = new HashMap<>();
            p.put("id", o.getAppointTimeId());
            AppointTime time = doctorDao.getAppointTimeById(p);
            orderResult.put("appointTime", time.getAppointDate() + "-" +time.getTimeSpan());
            orderResults.add(orderResult);
        }
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", orderResults);
        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/treat", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String treat(String treatResult, Integer orderId,String treatTime,@RequestParam("callback") String callback) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("id", orderId);
        param.put("treatResult", treatResult);
        param.put("treatTime", treatTime);
        appointOrderDao.treat(param);

        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data","");
        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value = "/saveUserInfo", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String saveUserInfo(User user,@RequestParam("callback") String callback) throws Exception{

        // User user = new User(userMap.get("telephone"), userDTO.getName(), userDTO.getGender(), userDTO.getBirthday(), userDTO.getHeight(), userDTO.getWeight(), userDTO.getJob(), userDTO.getEmergencyName(), userDTO.getEmergencyLink(), userDTO.getAddress(), userDTO.getHometown());

        userDao.updateBasicInfoById(user);
        logger.info("保存基本信息");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }

    @GetMapping(value="/adminLogin", produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String adminLogin(Integer id, String account, String password, @RequestParam("callback") String callback) throws Exception{
        logger.info(account);
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Integer> param = new HashMap<>();
        param.put("id", id);
        DoctorAdmin doctorAdminExist = doctorDao.getAdminDoctorById(param);
        if (doctorAdminExist != null){
            if (MD5EncodingHelper.EncodeByMD5(password).equals(doctorAdminExist.getPassword())){
                resultMap.put("status", "success");
                resultMap.put("errMsg", "");
                resultMap.put("data", id);
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
        String result = JSONObject.toJSONString(resultMap);
        return callback+"("+result +")";
    }
}
