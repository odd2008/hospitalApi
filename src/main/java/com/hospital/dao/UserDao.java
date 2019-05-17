package com.hospital.dao;

import com.hospital.entity.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    void addUser(User user);

    User getUserByTelephone(String telephone);

    void updateBasicInfo(User user);

    void updateBasicInfoById(User user);

    void addHeadImage(Map<String, String> imageInfo);
    List<User> getAllUser();
}
