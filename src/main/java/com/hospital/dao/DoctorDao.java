package com.hospital.dao;

import com.hospital.entity.AppointTime;
import com.hospital.entity.Doctor;

import java.util.List;
import java.util.Map;

public interface DoctorDao {
    List<Doctor> getDoctorByDepartId(Map<String, Integer> param);
    Doctor getDoctorById(Map<String, Integer> param);
    List<Doctor> getRecommendDoctor();
    List<AppointTime> getAppointTime(Map<String, Integer> param);
}
