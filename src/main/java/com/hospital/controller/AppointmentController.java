package com.hospital.controller;

import com.hospital.constant.Constants;
import com.hospital.dao.*;
import com.hospital.dto.AppointHistoryDTO;
import com.hospital.dto.DoctorDetailDTO;
import com.hospital.dto.UserDTO;
import com.hospital.entity.*;
import com.hospital.tools.AesEncryptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final static Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

    @Autowired
    private UserDao userDao;

    @Autowired
    private DepartDao departDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private AppointOrderDao appointOrderDao;

    @Autowired
    private ContentDao contentDao;

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
        List<Doctor> recommendDoctors = getRandomDoctor(4, doctors);

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

    @PostMapping(value = "/addAppointOrder")
    @Transactional
    public Map<String, Object> addAppointOrder(AppointOrder appointOrder, @RequestHeader HttpHeaders headers) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        String telephone = userMap.get("telephone");

        // 预约资格判断
        if(appointOrderDao.checkOrderExist(telephone).size() != 0) {
            resultMap.put("status", "error");
            resultMap.put("errMsg", "您已经预约，请勿重复预约");
            resultMap.put("data", "");
            return resultMap;
        }
        rwlock.writeLock().lock(); // 上写锁 单一线程访问
        Map<String, Integer> param = new HashMap<>();
        param.put("id", appointOrder.getAppointTimeId());
        AppointTime appointTime = doctorDao.getAppointTimeById(param);
        if(appointTime.getTotalNum() <= appointTime.getAppointNum()) {
            resultMap.put("status", "error");
            resultMap.put("errMsg", "预约名额已满");
            resultMap.put("data", "");
            return resultMap;
        }

        // 改变当前时间预约人数
        doctorDao.appointOrder(param);

        // 存订单数据库
        appointOrder.setTelephone(telephone);
        appointOrderDao.addOrder(appointOrder);
        rwlock.writeLock().unlock();  // 释放写锁

        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        return resultMap;
    }

    @PostMapping(value = "/getAppointHistory")
    public Map<String, Object> getAppointHistory(@RequestHeader HttpHeaders headers) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        logger.info(userMap.get("telephone"));
        String telephone = userMap.get("telephone");

        List<AppointOrder> appointOrder = appointOrderDao.getOrderByTelephone(telephone);
        List<AppointHistoryDTO> appointHistoryDTOs = new ArrayList<>();
        appointOrder.stream().forEach(order -> {
            // 获取医生信息
            Map<String, Integer> doctorParam = new HashMap<>();
            doctorParam.put("id", order.getDoctorId());
            Doctor doctor = doctorDao.getDoctorById(doctorParam);
            Map<String, Integer> departParam = new HashMap<>();
            departParam.put("id", doctor.getDepartId());
            Depart depart = departDao.getDepartById(departParam);
            DoctorDetailDTO doctorDetailDTO = new DoctorDetailDTO(doctor.getId(), doctor.getName(), doctor.getPosition(), doctor.getRate(), doctor.getAppointNum(), doctor.getSkills(), doctor.getHeadImageUrl(), depart.getName());

            // 获取挂号的时间段
            Map<String, Integer> appointParam = new HashMap<>();
            appointParam.put("id", order.getAppointTimeId());
            AppointTime appointTime = doctorDao.getAppointTimeById(appointParam);
            AppointHistoryDTO dto = new AppointHistoryDTO(order.getId(), doctorDetailDTO, appointTime, order.getTreatType(), order.getSickInfo(),order.getCreateTime(), order.getTreatTime(), order.getTreatResult(), order.getStatus());
            appointHistoryDTOs.add(dto);
        });
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", appointHistoryDTOs);
        return resultMap;
    }

    @PostMapping(value = "/cancleAppoint")
    @Transactional
    public Map<String, Object> cancleAppoint(Integer orderId, Integer timeId) {
        rwlock.writeLock().lock(); // 加写锁
        Map<String, Integer> orderParam = new HashMap<>();
        orderParam.put("id", orderId);
        appointOrderDao.cancleAppoint(orderParam);

        Map<String, Integer> timeParam = new HashMap<>();
        timeParam.put("id", timeId);
        doctorDao.cancleAppoint(timeParam);
        rwlock.writeLock().unlock();  // 释放写锁
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        return resultMap;
    }

    @PostMapping(value = "/addContent")
    public Map<String, Object> addContent(Content content, @RequestHeader HttpHeaders headers) throws Exception{
        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        String telephone = userMap.get("telephone");
        logger.info(telephone);

        content.setTelephone(telephone);
        if(content.getTargetType() == 2) {
            // 获取科室的id
            Map<String, Integer> doctorParam = new HashMap<>();
            doctorParam.put("id", content.getTargetId());
            Doctor doctor = doctorDao.getDoctorById(doctorParam);
            content.setTargetId(doctor.getDepartId());
        }
        Map<String, Object> param = new HashMap<>();
        param.put("targetId", content.getTargetId());
        param.put("targetType", content.getTargetType());
        param.put("telephone", telephone);
        if(contentDao.getCommentById(param) != null) {
            // 修改
            logger.info("修改");
            contentDao.updateContent(content);
        } else {
            logger.info("添加");
            contentDao.addContent(content);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", "");
        return resultMap;
    }

    @PostMapping(value = "/getComment")
    public Map<String, Object> getComment(Integer targetType, Integer targetId) {

        Map<String, Integer> param = new HashMap<>();
        param.put("targetType", targetType);
        param.put("targetId", targetId);
        List<Content> contents = contentDao.getComment(param);
        List<Map<String, Object>> resultList = new ArrayList<>();
        contents.stream().forEach(content -> {
            Map<String, Object> result = new HashMap<>();
            User user = userDao.getUserByTelephone(content.getTelephone());
            UserDTO userDTO = new UserDTO(user, false);
            result.put("user", userDTO);
            result.put("comment", content);
            resultList.add(result);
        });

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", resultList);
        return resultMap;
    }

    @PostMapping(value = "/getCurrentContent")
    public Map<String, Object> getCurrentContent(Integer doctorId, @RequestHeader HttpHeaders headers) throws Exception {

        //获取用户电话号码
        String authorizationToken = headers.getFirst(Constants.AUTHORIZATION);
        Map<String, String> userMap = AesEncryptHelper.getUserFromToken(authorizationToken);
        String telephone = userMap.get("telephone");

        // 医生评论
        Map<String, Object> param = new HashMap<>();
        param.put("targetId", doctorId);
        param.put("targetType", 1);
        param.put("telephone", telephone);
        Content contentDoctor = contentDao.getCommentById(param);

        // 科室评论
        // 获取科室的id
        Map<String, Integer> doctorParam = new HashMap<>();
        doctorParam.put("id", doctorId);
        Doctor doctor = doctorDao.getDoctorById(doctorParam);

        param.put("targetId", doctor.getDepartId());
        param.put("targetType", 2);
        Content contentDepart = contentDao.getCommentById(param);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("doctor", contentDoctor);
        resultData.put("depart", contentDepart);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("status", "success");
        resultMap.put("errMsg", "");
        resultMap.put("data", resultData);
        return resultMap;
    }
}
