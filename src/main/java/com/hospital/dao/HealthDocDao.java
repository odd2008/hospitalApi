package com.hospital.dao;

import com.hospital.entity.HealthDoc;
import com.hospital.entity.HealthDocImage;

import java.util.List;
import java.util.Map;

public interface HealthDocDao {

    void addHealthDoc(HealthDoc healthDoc);

    void addHealthDocImage(HealthDocImage healthDocImage);

    HealthDoc getHealthDocByTelephone(String telephone);

    List<HealthDocImage> getHealthDocImageByHealthId(Map<String,Integer> param);

    void updateHealthDoc(HealthDoc healthDoc);

    void deleteHeadImage(Integer id);
}
