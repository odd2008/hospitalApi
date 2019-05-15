package com.hospital.dao;

import com.hospital.entity.AppointOrder;

import java.util.List;

public interface AppointOrderDao {

    void addOrder(AppointOrder appointOrder);

    List<AppointOrder> getOrderByTelephone(String telephone);

    void updateOrder(AppointOrder appointOrder);

    List<AppointOrder> checkOrderExist(String telephone);
}
