package com.hospital.entity;

import java.util.Date;

public class HealthDoc {
    private Integer id;
    private String hospitalHistory;
    private String operationHistory;
    private String familyHistory;
    private Date createTime;
    private String telephone;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
