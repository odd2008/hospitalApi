package com.hospital.controller;

import com.hospital.constant.Constants;
import com.hospital.dao.HealthDocDao;
import com.hospital.dao.UserDao;
import com.hospital.entity.HealthDoc;
import com.hospital.tools.AesEncryptHelper;
import com.hospital.tools.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/healthDoc")
public class HealthDocController {
    private final static Logger logger = LoggerFactory.getLogger(HealthDocController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private HealthDocDao healthDocDao;

    @Autowired
    private MessageProperties config; //用来获取file-message.properties配置文件中的信息

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
}
