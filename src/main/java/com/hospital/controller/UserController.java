package com.hospital.controller;

import com.hospital.constant.Constants;
import com.hospital.dao.UserDao;
import com.hospital.dto.UserDTO;
import com.hospital.entity.User;
import com.hospital.tools.*;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageProperties config; //用来获取file-message.properties配置文件中的信息

    @PostMapping(value = "/test")
    public Map<String, Object> test(@RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        User user = userDao.getUserByTelephone(userMap.get("telephone"));

        logger.info("test");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        return resultMap;
    }

    @PostMapping(value = "/getUserInfo")
    public Map<String, Object> getUserInfo(@RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        User user = userDao.getUserByTelephone(userMap.get("telephone"));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        UserDTO userDTO = new UserDTO(user, false);
        resultMap.put("data", userDTO);
        return resultMap;
    }

    @PostMapping(value = "/saveUserInfo")
    public Map<String, Object> saveUserInfo(UserDTO userDTO, @RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        User user = new User(userMap.get("telephone"), userDTO.getName(), userDTO.getGender(), userDTO.getBirthday(), userDTO.getHeight(), userDTO.getWeight(), userDTO.getJob(), userDTO.getEmergencyName(), userDTO.getEmergencyLink(), userDTO.getAddress(), userDTO.getHometown());

        userDao.updateBasicInfo(user);
        logger.info("保存基本信息");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        return resultMap;
    }

    @PostMapping(value = "/uploadHeadImage")
    public Map<String, Object> uploadHeadImage(@RequestParam("file") MultipartFile multipartFile, @RequestHeader HttpHeaders headers) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        try {
            User user=userDao.getUserByTelephone(userMap.get("telephone"));
            if (!multipartFile.isEmpty()) {
                resultMap = uploadPicture(multipartFile, userMap.get("telephone"));
            } else {
                logger.info(">>>>>>上传图片为空文件");
                resultMap.put("status", "error"); // success error
                resultMap.put("errMsg", "上传图片为空文件");
                resultMap.put("data", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(">>>>>>图片上传异常，e={}", e.getMessage());
            resultMap.put("status", "error"); // success error
            resultMap.put("errMsg", "图片上传异常");
            resultMap.put("data", "");
        }
        return resultMap;
    }

    private Map<String, Object> uploadPicture(MultipartFile file,String telephone) throws Exception {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            String[] IMAGE_TYPE = config.getImageType().split(",");
            String path = null;
            boolean flag = false;
            for (String type : IMAGE_TYPE) {
                if (StringUtils.endsWithIgnoreCase(file.getOriginalFilename(), type)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                // 获得文件类型
                String fileType = file.getContentType();
                // 获得文件后缀名称
                String imageName = fileType.substring(fileType.indexOf("/") + 1);
                // 原名称
                String oldFileName = file.getOriginalFilename();
                // 新名称
                String newFileName = uuid + "." + imageName;
                // 年月日文件夹
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String basedir = sdf.format(new Date());
                // 进行压缩(大于4M)
                if (file.getSize() > config.getFileSize()) {
                    // 重新生成
                    String newUUID = UUID.randomUUID().toString().replaceAll("-", "");
                    newFileName = newUUID + "." + imageName;
                    path = config.getUpPath() + "/" + telephone + "/" + basedir + "/" + newUUID + "." + imageName;
                    // 如果目录不存在则创建目录
                    File oldFile = new File(path);
                    if (!oldFile.exists()) {
                        oldFile.mkdirs();
                    }
                    file.transferTo(oldFile);
                    // 压缩图片
                    Thumbnails.of(oldFile).scale(config.getScaleRatio()).toFile(path);
                    // 显示路径
                    String finalPath = "/" + telephone + "/" + basedir + "/" + newUUID + "." + imageName;
                    resultMap.put("status", "success"); // success error
                    resultMap.put("errMsg", "");
                    resultMap.put("data", finalPath);
                } else {
                    path = config.getUpPath()+ "/" + telephone  + "/" + basedir + "/" + uuid + "." + imageName;
                    // 如果目录不存在则创建目录
                    File uploadFile = new File(path);
                    if (!uploadFile.exists()) {
                        uploadFile.mkdirs();
                    }
                    file.transferTo(uploadFile);
                    // 显示路径
                    String finalPath ="/" + telephone + "/" + basedir + "/" + uuid + "." + imageName;
                    resultMap.put("status", "success"); // success error
                    resultMap.put("errMsg", "");
                    resultMap.put("data", finalPath);
                }
            } else {
                resultMap.put("status", "error"); // success error
                resultMap.put("errMsg", "图片格式不正确,支持png|jpg|jpeg");
                resultMap.put("data", "");
                return resultMap;
            }
            //图片上传成功，将图片路径写入数据库
            Map<String, String> param = new HashMap<>();
            param.put("headImageUrl", resultMap.get("data").toString());
            param.put("telephone", telephone);
            //添加图片路径
            userDao.addHeadImage(param);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
