package com.hospital.entity;

public class HealthDocImage {
    private Integer id;
    private String imageUrl;
    private String content;
    private String createDate;
    private Integer healthId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getHealthId() {
        return healthId;
    }

    public void setHealthId(Integer healthId) {
        this.healthId = healthId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
