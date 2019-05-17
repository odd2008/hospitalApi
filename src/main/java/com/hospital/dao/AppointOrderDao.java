package com.hospital.dao;

import com.hospital.entity.AppointOrder;

import java.util.List;
import java.util.Map;

public interface AppointOrderDao {

    void addOrder(AppointOrder appointOrder);

    List<AppointOrder> getOrderByTelephone(String telephone);

    void updateOrder(AppointOrder appointOrder);

    List<AppointOrder> checkOrderExist(String telephone);

    void cancleAppoint(Map<String, Integer> param);

    List<AppointOrder> getAppoint(Map<String, Integer> param);
    void treat(Map<String, Object> param);


}
