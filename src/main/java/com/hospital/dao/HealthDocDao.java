package com.hospital.dao;

import com.hospital.entity.HealthDoc;
import com.hospital.entity.HealthDocImage;

import java.util.List;

public interface HealthDocDao {

    void addHealthDoc(HealthDoc healthDoc);

    void addHealthDocImage(HealthDocImage healthDocImage);

    HealthDoc getHealthDocByTelephone(String telephone);

    List<HealthDocImage> getHealthDocImageByHealthId(Integer healthId);

    void updateHealthDoc(HealthDoc healthDoc);

    void deleteHeadImage(Integer id);
}
