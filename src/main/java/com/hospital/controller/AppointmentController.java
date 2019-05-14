package com.hospital.controller;

import com.hospital.constant.Constants;
import com.hospital.dao.DepartDao;
import com.hospital.dao.DoctorDao;
import com.hospital.dao.UserDao;
import com.hospital.dto.DoctorDetailDTO;
import com.hospital.entity.*;
import com.hospital.tools.AesEncryptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final static Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private DepartDao departDao;

    @Autowired
    private DoctorDao doctorDao;

    @PostMapping(value = "/getDepartInfo")
    public Map<String, Object> getDepartInfo(@RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));

        logger.info("getDepartInfo");
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> datas = new ArrayList<>();
        List<DepartType> departTypes = departDao.getDepartType();
        departTypes.stream().forEach((type) -> {
            Map<String, Integer> param  = new HashMap<>();
            param.put("typeId", type.getId());
            List<Depart> departs = departDao.getDepart(param);
            Map<String, Object> data = new HashMap<>();
            data.put("name", type.getName());
            data.put("types", departs);
            datas.add(data);
        });

        resultMap.put("status", "success"); // success error
        resultMap.put("errMsg", "");
        resultMap.put("data",datas);
        return resultMap;
    }

    @PostMapping(value = "/getDoctorInfo")
    public Map<String, Object> getDoctorInfo(Integer departId, @RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        String telephone = userMap.get("telephone");

        logger.info("getDoctorInfo");
        Map<String, Integer> param = new HashMap<>();
        param.put("departId", departId);
        List<Doctor> doctors = doctorDao.getDoctorByDepartId(param);
        List<DoctorDetailDTO> doctorDetailDTOs = new ArrayList<>();
        doctors.stream().forEach(doctor -> {
            Map<String, Integer> countParam = new HashMap<>();
            countParam.put("doctorId", doctor.getId());
            Integer count = doctorDao.getAppointCount(countParam);
            if (count == null) {
                count = 0;
            }
            DoctorDetailDTO doctorDetailDTO = new DoctorDetailDTO(doctor.getId(), doctor.getName(), doctor.getPosition(), doctor.getRate(), doctor.getAppointNum(), doctor.getSkills(), doctor.getHeadImageUrl(), "");
            doctorDetailDTO.setCount(count);
            doctorDetailDTOs.add(doctorDetailDTO);
        });
        logger.info(departId.toString());
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", doctorDetailDTOs);
        return resultMap;
    }

    @PostMapping(value = "/getDoctorDetail")
    public Map<String, Object> getDoctorDetail(Integer id, @RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        String telephone = userMap.get("telephone");

        logger.info("getDoctorInfo");
        Map<String, Integer> doctorParam = new HashMap<>();
        doctorParam.put("id", id);
        logger.info(id.toString());
        Doctor doctor = doctorDao.getDoctorById(doctorParam);
        Map<String, Integer> departParam = new HashMap<>();
        departParam.put("id", doctor.getDepartId());
        Depart depart = departDao.getDepartById(departParam);

        DoctorDetailDTO doctorDetailDTO = new DoctorDetailDTO(doctor.getId(), doctor.getName(), doctor.getPosition(), doctor.getRate(), doctor.getAppointNum(), doctor.getSkills(), doctor.getHeadImageUrl(), depart.getName());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", doctorDetailDTO);
        return resultMap;
    }

    @PostMapping(value = "/getAppointCount")
    public Map<String, Object> getAppointCount(Integer doctorId) throws Exception{
        Map<String, Integer> countParam = new HashMap<>();
        countParam.put("id", doctorId);
        Integer count = doctorDao.getAppointCount(countParam);
        if (count == null) {
            count = 0;
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", count);
        return resultMap;
    }

    @PostMapping(value = "/getRecommendDoctor")
    public Map<String, Object> getRecommendDoctor() throws Exception{
        logger.info("getRecommendDoctor");
        List<Doctor> doctors = doctorDao.getRecommendDoctor(); // 预选20个优秀的同志
        List<Doctor> recommendDoctors = getRandomDoctor(2, doctors);

        List<DoctorDetailDTO> recommendDoctorsDetail = new ArrayList<>();
        for (Doctor doctor: recommendDoctors) {
            Map<String, Integer> departParam = new HashMap<>();
            departParam.put("id", doctor.getDepartId());
            Depart depart = departDao.getDepartById(departParam);
            recommendDoctorsDetail.add(new DoctorDetailDTO(doctor.getId(), doctor.getName(), doctor.getPosition(), doctor.getRate(), doctor.getAppointNum(), doctor.getSkills(), doctor.getHeadImageUrl(), depart.getName()));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", recommendDoctorsDetail);
        return resultMap;
    }

    private List<Doctor> getRandomDoctor(int length, List<Doctor> doctors) {
        if(length >= doctors.size()) {
            return doctors;
        }
        List<Doctor> recommendDoctors = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int max = doctors.size();
            int randomIndex = (int)(0 + Math.random() * max);
            recommendDoctors.add(doctors.get(randomIndex));
            logger.info("随机数：" + String.valueOf(randomIndex));
            doctors.remove(randomIndex);
        }
        return recommendDoctors;
    }

    @PostMapping(value = "/getAppointTime")
    public Map<String, Object> getAppointTime(Integer doctorId) {

        Map<String, Integer> param = new HashMap<>();
        param.put("doctorId", doctorId);
        List<AppointTime> appointTimes = doctorDao.getAppointTime(param);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", appointTimes);
        return resultMap;
    }

}
