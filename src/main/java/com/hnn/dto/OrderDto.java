package com.hnn.dto;

import com.hnn.entity.OrderDetail;
import com.hnn.entity.Orders;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails=new ArrayList<>();

}
