package com.hnn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnn.entity.Orders;
import org.springframework.core.annotation.Order;

public interface IOrderService extends IService<Orders> {

    /**
     * 提交订单*
     * @param orders
     */
    public void submit(Orders orders);

}
