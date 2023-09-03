package com.hnn.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.entity.OrderDetail;
import com.hnn.mapper.OrderDetailMapper;
import com.hnn.service.IOrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {
}
