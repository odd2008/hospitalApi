package com.hospital.entity;

public class AppointOrder {
    private Integer id;
    private Integer doctorId;
    private Integer appointTimeId;
    private String telephone;
    private String treatType;
    private String sickInfo;
    private String createTime;
    private String treatTime;
    private String treatResult;
    private Integer status; // 0等待就诊  1 已就诊  2  挂号取消

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getAppointTimeId() {
        return appointTimeId;
    }

    public void setAppointTimeId(Integer appointTimeId) {
        this.appointTimeId = appointTimeId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTreatType() {
        return treatType;
    }

    public void setTreatType(String treatType) {
        this.treatType = treatType;
    }

    public String getSickInfo() {
        return sickInfo;
    }

    public void setSickInfo(String sickInfo) {
        this.sickInfo = sickInfo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTreatTime() {
        return treatTime;
    }

    public void setTreatTime(String treatTime) {
        this.treatTime = treatTime;
    }

    public String getTreatResult() {
        return treatResult;
    }

    public void setTreatResult(String treatResult) {
        this.treatResult = treatResult;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
