package com.hospital.dao;

import com.hospital.entity.Content;

import java.util.List;
import java.util.Map;

public interface ContentDao {
    void addContent(Content content);

    List<Content> getComment(Map<String, Integer> param);

    Content getCommentById(Map<String, Object> param);

    void updateContent(Content content);
}
