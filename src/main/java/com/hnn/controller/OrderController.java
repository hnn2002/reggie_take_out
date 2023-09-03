package com.hnn.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.hnn.common.BaseContext;
import com.hnn.common.R;
import com.hnn.dto.OrderDto;
import com.hnn.entity.OrderDetail;
import com.hnn.entity.Orders;
import com.hnn.entity.ShoppingCart;
import com.hnn.service.IOrderDetailService;
import com.hnn.service.IOrderService;
import com.hnn.service.IShoppingCartService;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IShoppingCartService shoppingCartService;
    /**
     * 提交订单*
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("Order:{}",orders.toString());

        orderService.submit(orders);

        return R.success("下单成功！");
    }

    /**
     * 移动端分页显示订单*
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(@RequestParam(value="page") int currentPage, @RequestParam(value="pageSize") int pageSize){
        log.info("currentPage:{},pageSize:{}",currentPage,pageSize);

        Page<Orders> page=new Page<Orders>(currentPage,pageSize);

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据当前用户id获取当前用户订单记录
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<Orders>();
        lambdaQueryWrapper.eq(userId!=null,Orders::getUserId,userId);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(page,lambdaQueryWrapper);

        //新建pageDto对象
        Page<OrderDto> orderDtoPage=new Page<OrderDto>();

        //将page对象处record属性外的其他属性复制给pageDto对象
        BeanUtils.copyProperties(page,orderDtoPage,"records");

        //对orderDtoPage的records单独处理
        List<Orders> orders = page.getRecords();

        List<OrderDto> ordersDto = orders.stream().map((item) -> {
            //新建一个orderDto对象
            OrderDto orderDto = new OrderDto();

            //将order对象的所有值复制给orderDto
            BeanUtils.copyProperties(item, orderDto);

            //获取当前order(订单)的id
            Long orderId = orderDto.getId();

            //根据订单id获取当前订单对应订单明细
            LambdaQueryWrapper<OrderDetail> orderDetailLqw = new LambdaQueryWrapper<OrderDetail>();
            orderDetailLqw.eq(orderId != null, OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLqw);

            //将查询到的订单明细复制给orderDto对象
            orderDto.setOrderDetails(orderDetails);

            return orderDto;
        }).collect(Collectors.toList());

        //设置orderDtoPage的records属性
        orderDtoPage.setRecords(ordersDto);

        return R.success(orderDtoPage);
    }

    /**
     * 再来一单*
     * @param order
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders order){
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据当前订单号获取对应的订单实体
        order=orderService.getById(order.getId());

        if(order==null){
            return R.error("当前订单不存在！");
        }

        //根据当前订单号获取对应的订单明细数据
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper=new LambdaQueryWrapper<OrderDetail>();
        lambdaQueryWrapper.eq(order.getId()!=null,OrderDetail::getOrderId,order.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper);

        //将订单对应的数据添加到用户购物车
        List<ShoppingCart> shoppingCartList = orderDetails.stream().map((item) -> {
            //新建一个购物车对象
            ShoppingCart shoppingCart = new ShoppingCart();

            //设置购物车对象的属性
            shoppingCart.setName(item.getName());//名称
            shoppingCart.setImage(item.getImage());//图片
            shoppingCart.setUserId(userId);//用户Id
            shoppingCart.setDishId(item.getDishId());//菜品id
            shoppingCart.setSetmealId(item.getSetmealId());//套餐id
            shoppingCart.setDishFlavor(item.getDishFlavor());//菜品口味
            shoppingCart.setNumber(item.getNumber());//数量
            shoppingCart.setAmount(item.getAmount());//金额
            shoppingCart.setCreateTime(LocalDateTime.now());//创建时间

            return shoppingCart;
        }).collect(Collectors.toList());

        //将当前用户的购物车清空
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

        //插入再来一单的购物车数据
        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("再来一单成功！");
    }

    /**
     * 用户端分页显示订单*
     * @param currentPage
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam(value="page") int currentPage,@RequestParam(value="pageSize") int pageSize,@RequestParam(value="number",required = false) Long number,@RequestParam(value="beginTime",required = false) String beginTime,@RequestParam(value="endTime",required = false) String endTime){
        log.info("currentPage:{},pageSize:{},number:{},beginTime:{},endTime:{}",currentPage,pageSize,number,beginTime,endTime);

        //创建page对象
        Page page=new Page(currentPage,pageSize);


        //根据条件查询符合条件的订单
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<Orders>();
        lambdaQueryWrapper.eq(number!=null,Orders::getNumber,number);
        lambdaQueryWrapper.between(Strings.isNotEmpty(beginTime) && Strings.isNotEmpty(endTime),Orders::getOrderTime,beginTime,endTime);
        //按照下单时间排序
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(page,lambdaQueryWrapper);


        return R.success(page);
    }

    /**
     * 修改订单状态*
     * @param order
     * @return
     */
    @PutMapping
    public R<String> modifyStatus(@RequestBody Orders order){
        log.info("orders:{}",order.toString());

        //传入订单为空则报错
        if(order==null){
            return R.error("要修改的订单不存在！");
        }

        //更新订单
        orderService.updateById(order);
        return R.success("修改订单状态成功！");
    }
}
