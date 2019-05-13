package com.hospital.dao;

import com.hospital.entity.User;

import java.util.Map;

public interface UserDao {
    void addUser(User user);

    User getUserByTelephone(String telephone);

    void updateBasicInfo(User user);

    void addHeadImage(Map<String, String> imageInfo);
}
