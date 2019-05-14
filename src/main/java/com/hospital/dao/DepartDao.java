package com.hospital.dao;

import com.hospital.entity.Depart;
import com.hospital.entity.DepartType;

import java.util.List;
import java.util.Map;

public interface DepartDao {
    List<DepartType> getDepartType();

    List<Depart> getDepart(Map<String, Integer> param);

    Depart getDepartById(Map<String, Integer> param);
}
