package com.hnn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.common.BaseContext;
import com.hnn.common.CustomException;
import com.hnn.entity.*;
import com.hnn.mapper.OrderMapper;
import com.hnn.service.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders>implements IOrderService {

    @Autowired
    private IShoppingCartService shoppingCartService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IAddressBookService addressBookService;

    @Autowired
    private IOrderDetailService orderDetailService;

    /**
     * 提交订单*
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据当前用户id查询购物车
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        lambdaQueryWrapper.eq(userId!=null,ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lambdaQueryWrapper);

        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车为空，不能下单！");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            new CustomException("用户地址信息有误，不能下单！");
        }

        //设置订单号(此处要设置order_detail的id,因此要自己生成id)
        long orderId = IdWorker.getId();

        //设置订单金额
        AtomicInteger amount=new AtomicInteger(0);

        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            //创建order_detail对象
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setName(item.getName());//菜品/套餐名
            orderDetail.setImage(item.getImage());//图片
            orderDetail.setOrderId(orderId);//订单号
            orderDetail.setDishId(item.getDishId());//菜品号
            orderDetail.setSetmealId(item.getSetmealId());//套餐号
            orderDetail.setDishFlavor(item.getDishFlavor());//菜品口味
            orderDetail.setNumber(item.getNumber());//数量
            orderDetail.setAmount(item.getAmount());//菜品/套餐单价

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//订单总金额

            return orderDetail;
        }).collect(Collectors.toList());

        //红色纸

        //向订单表插入一条数据
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));//订单号
        orders.setStatus(2);//订单状态
        orders.setUserId(userId);//用户id
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));//订单金额
        orders.setPhone(addressBook.getPhone());//电话


        orders.setAddress((addressBook.getProvinceName()==null?"":addressBook.getProvinceName())+
                (addressBook.getCityName()==null?"":addressBook.getCityName())+
                (addressBook.getDistrictName()==null?"":addressBook.getDistrictName())+
                (addressBook.getDetail()==null?"":addressBook.getDetail()));//地址
        orders.setUserName(user.getName());//用户名
        orders.setConsignee(addressBook.getConsignee());//收货人

        this.save(orders);

        //向订单明细表插（多条）入数据
        orderDetailService.saveBatch(orderDetailList);

        //清空当前用户购物车
        shoppingCartService.remove(lambdaQueryWrapper);
    }
}
