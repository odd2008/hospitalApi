package com.hospital.dto;

import com.hospital.entity.AppointTime;

public class AppointHistoryDTO {
    private Integer id;
    private DoctorDetailDTO doctorDTO;
    private AppointTime appointTime;
    private String treatType;
    private String sickInfo;
    private String createTime;
    private String treatTime;
    private String treatResult;
    private Integer status; // 0等待就诊  1 已就诊  2  挂号取消

    public AppointHistoryDTO(Integer id, DoctorDetailDTO doctorDTO, AppointTime appointTime, String treatType, String sickInfo, String createTime, String treatTime, String treatResult, Integer status) {
        this.id = id;
        this.doctorDTO = doctorDTO;
        this.appointTime = appointTime;
        this.treatType = treatType;
        this.sickInfo = sickInfo;
        this.createTime = createTime;
        this.treatTime = treatTime;
        this.treatResult = treatResult;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DoctorDetailDTO getDoctorDTO() {
        return doctorDTO;
    }

    public void setDoctorDTO(DoctorDetailDTO doctorDTO) {
        this.doctorDTO = doctorDTO;
    }

    public AppointTime getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(AppointTime appointTime) {
        this.appointTime = appointTime;
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
