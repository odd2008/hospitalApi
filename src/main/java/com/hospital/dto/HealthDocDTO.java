package com.hospital.dto;

import com.hospital.entity.HealthDoc;

public class HealthDocDTO {
    private Integer id;
    private String hospitalHistory;
    private String operationHistory;
    private String familyHistory;
    private String createTime;
    private String telephone;

    public HealthDocDTO() {}

    public  HealthDocDTO(HealthDoc healthDoc) {
        this.id = healthDoc.getId();
        this.familyHistory = healthDoc.getFamilyHistory();
        this.hospitalHistory = healthDoc.getHospitalHistory();
        this.operationHistory = healthDoc.getOperationHistory();
        this.telephone = healthDoc.getTelephone();
        this.createTime = healthDoc.getCreateTime().toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHospitalHistory() {
        return hospitalHistory;
    }

    public void setHospitalHistory(String hospitalHistory) {
        this.hospitalHistory = hospitalHistory;
    }

    public String getOperationHistory() {
        return operationHistory;
    }

    public void setOperationHistory(String operationHistory) {
        this.operationHistory = operationHistory;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
