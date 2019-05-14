package com.hospital.controller;

import com.hospital.constant.Constants;
import com.hospital.dao.HealthDocDao;
import com.hospital.dao.UserDao;
import com.hospital.dto.HealthDocDTO;
import com.hospital.entity.HealthDoc;
import com.hospital.entity.HealthDocImage;
import com.hospital.entity.User;
import com.hospital.tools.AesEncryptHelper;
import com.hospital.tools.MessageProperties;
import com.hospital.tools.UploadHelper;
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
import java.util.*;

@RestController
@RequestMapping("/healthDoc")
public class HealthDocController {
    private final static Logger logger = LoggerFactory.getLogger(HealthDocController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageProperties config; //用来获取file-message.properties配置文件中的信息

    @Autowired
    private HealthDocDao healthDocDao;

    @PostMapping(value = "/saveHealthDoc")
    public Map<String, Object> saveHealthDoc(HealthDoc healthDoc, @RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);

        logger.info("saveHealthDoc");
        String telephone = userMap.get("telephone");
        Map<String, Object> resultMap = new HashMap<>();
        healthDoc.setTelephone(telephone);
        HealthDoc healthDocDb = healthDocDao.getHealthDocByTelephone(telephone);
        if(healthDocDb != null){
            healthDocDb.setFamilyHistory(healthDoc.getFamilyHistory());
            healthDocDb.setHospitalHistory(healthDoc.getHospitalHistory());
            healthDocDb.setOperationHistory(healthDoc.getOperationHistory());
            healthDocDao.updateHealthDoc(healthDocDb);
            logger.info("update");
        } else {
            healthDocDao.addHealthDoc(healthDoc);
            logger.info("add");
        }
        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        return resultMap;
    }

    @PostMapping(value = "/getHealthDoc")
    public Map<String, Object> getHealthDoc(@RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);

        logger.info("getHealthDoc");
        String telephone = userMap.get("telephone");
        Map<String, Object> resultMap = new HashMap<>();
        HealthDoc healthDocDb = healthDocDao.getHealthDocByTelephone(telephone);
        if(healthDocDb != null) {
            resultMap.put("status", "success"); // success error
            resultMap.put("errMsg", "");
            HealthDocDTO healthDocDTO = new HealthDocDTO(healthDocDb);
            resultMap.put("data", healthDocDTO);
        } else {
            resultMap.put("status", "error"); // success error
            resultMap.put("errMsg", "暂无数据");
            resultMap.put("data", "");
        }

        return resultMap;
    }

    @PostMapping(value = "/getHealthImage")
    public Map<String, Object> getHealthImage(@RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);

        logger.info("getHealthImage");
        String telephone = userMap.get("telephone");
        Map<String, Object> resultMap = new HashMap<>();
        HealthDoc healthDocDb = healthDocDao.getHealthDocByTelephone(telephone);
        if(healthDocDb != null) {
            Map<String,Integer> param = new HashMap<>();
            param.put("healthId", healthDocDb.getId());
            List<HealthDocImage> healthDocImages = healthDocDao.getHealthDocImageByHealthId(param);
            resultMap.put("status", "success"); // success error
            resultMap.put("errMsg", "");
            resultMap.put("data", healthDocImages);
        } else {
            resultMap.put("status", "error"); // success error
            resultMap.put("errMsg", "暂无数据");
            resultMap.put("data", "");
        }

        return resultMap;
    }

    @PostMapping(value = "/deleteHealthImage")
    public Map<String, Object> deleteHealthImage(Integer id, @RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);

        logger.info("deleteHealthImage");
        Map<String, Object> resultMap = new HashMap<>();
        healthDocDao.deleteHeadImage(id);
        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        return resultMap;
    }

    @PostMapping(value = "/uploadReportImage")
    public Map<String, Object> uploadReportImage(@RequestParam("file") MultipartFile multipartFile, String content, String createDate, @RequestHeader HttpHeaders headers) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        String telephone = userMap.get("telephone");
        try {
            if (!multipartFile.isEmpty()) {
                resultMap = UploadHelper.uploadPicture(multipartFile, config, telephone, "report");
                //图片上传成功，将图片路径写入数据库
                // 先取基础数据
                HealthDoc healthDocDb = healthDocDao.getHealthDocByTelephone(telephone);
                if(healthDocDb == null) {
                    // 如果基础数据不存在，就添加
                    healthDocDb = new HealthDoc();
                    healthDocDb.setOperationHistory("");
                    healthDocDb.setHospitalHistory("");
                    healthDocDb.setFamilyHistory("");
                    healthDocDb.setTelephone(telephone);
                    healthDocDao.addHealthDoc(healthDocDb);
                }
                HealthDocImage healthDocImage = new HealthDocImage();
                healthDocImage.setImageUrl(resultMap.get("data").toString());
                healthDocImage.setCreateDate(createDate);
                healthDocImage.setContent(content);
                healthDocImage.setHealthId(healthDocDb.getId());
                //添加图片路径
                healthDocDao.addHealthDocImage(healthDocImage);
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
}
