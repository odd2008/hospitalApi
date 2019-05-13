package com.hospital.dto;

import com.hospital.entity.User;


public class UserDTO {
    // 用户账户信息
    private Integer id;
    private String telephone;
    private String passwordHash;
    private String passwordSalt;
    private String mail;
    // 用户基本信息
    private String name;
    private String gender;
    private String birthday;
    private Double height;
    private Double weight;
    private String job;
    private String emergencyName;
    private String emergencyLink;
    private String address;
    private String hometown;
    private String createTime;
    private String headImageUrl;

    public UserDTO(){}

    public UserDTO(Integer id, String telephone, String passwordHash, String passwordSalt, String mail, String name, String gender, String birthday, Double height, Double weight, String job, String emergencyName, String emergencyLink, String address, String hometown, String createTime, String headImageUrl) {
        this.id = id;
        this.telephone = telephone;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.mail = mail;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.job = job;
        this.emergencyName = emergencyName;
        this.emergencyLink = emergencyLink;
        this.address = address;
        this.hometown = hometown;
        this.createTime = createTime;
        this.headImageUrl = headImageUrl;
    }

    public UserDTO(User user, boolean isAccount) {
        if(isAccount) {
            this.telephone = user.getTelephone();
            this.passwordHash = user.getPasswordHash();
            this.passwordSalt = user.getPasswordSalt();
            this.mail = user.getMail();
        }
        this.id = user.getId();

        this.name = user.getName();
        this.gender = user.getGender();
        this.birthday = user.getBirthday();
        this.height = user.getHeight();
        this.weight = user.getWeight();
        this.job = user.getJob();
        this.emergencyName = user.getEmergencyName();
        this.emergencyLink = user.getEmergencyLink();
        this.address = user.getAddress();
        this.hometown = user.getAddress();
        this.createTime = user.getCreateTime().toString();
        this.headImageUrl = user.getHeadImageUrl();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getEmergencyLink() {
        return emergencyLink;
    }

    public void setEmergencyLink(String emergencyLink) {
        this.emergencyLink = emergencyLink;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }
}
