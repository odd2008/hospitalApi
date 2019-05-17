package com.hospital.dao;

import com.hospital.entity.AppointTime;
import com.hospital.entity.Doctor;
import com.hospital.entity.DoctorAdmin;
import com.hospital.entity.DoctorDepart;

import java.util.List;
import java.util.Map;

public interface DoctorDao {

    List<Doctor> getDoctorByDepartId(Map<String, Integer> param);

    Doctor getDoctorById(Map<String, Integer> param);

    List<Doctor> getRecommendDoctor();

    List<AppointTime> getAppointTime(Map<String, Integer> param);

    Integer getAppointCount(Map<String, Integer> param);

    void appointOrder(Map<String, Integer> param);

    AppointTime getAppointTimeById(Map<String, Integer> param);

    void cancleAppoint(Map<String, Integer> param);

    List<DoctorDepart> getAllDoctor();

    DoctorAdmin getAdminDoctorById(Map<String, Integer> param);

    void addAppointTime(AppointTime appointTime);

    void delAppointTime(Map param);
}
