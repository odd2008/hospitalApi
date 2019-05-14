package com.hospital.dto;

public class DoctorDetailDTO {
    private Integer id;
    private String name;
    private String position;
    private Double rate;
    private Integer appointNum;
    private String skills;
    private String headImageUrl;
    private String department;
    private Integer count;

    public DoctorDetailDTO(Integer id, String name, String position, Double rate, Integer appointNum, String skills, String headImageUrl, String department) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.rate = rate;
        this.appointNum = appointNum;
        this.skills = skills;
        this.headImageUrl = headImageUrl;
        this.department = department;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getAppointNum() {
        return appointNum;
    }

    public void setAppointNum(Integer appointNum) {
        this.appointNum = appointNum;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
