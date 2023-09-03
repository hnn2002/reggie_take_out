package com.hnn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.hnn.common.BaseContext;
import com.hnn.common.R;
import com.hnn.entity.ShoppingCart;
import com.hnn.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 展示当前用户购物车所有数据*
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据当前用户id查询该用户下购物车内所有数据
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        lambdaQueryWrapper.eq(userId!=null,ShoppingCart::getUserId,userId);
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lambdaQueryWrapper);

        return R.success(shoppingCartList);
    }

    /**
     * 保存用户添加的购物车数据*
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:{}",shoppingCart.toString());

        //设置购物车的userId属性
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询当前菜品或套餐是否已经在当前用户的购物车中
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if(shoppingCart.getDishId()!=null){
            //添加到购物车的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());

        }else{
            //添加到购物车的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询套餐/菜品是否已存在
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);

        if(shoppingCartServiceOne!=null){
            //套餐/菜品已存在，将其number在原来的基础上加1
            shoppingCartServiceOne.setNumber(shoppingCartServiceOne.getNumber()+1);
            //因为前端传过来的shoppingCart的id空，所以使用shoppingCartServiceOne
            shoppingCartService.updateById(shoppingCartServiceOne);
        }else{
            //套餐/菜品不存在，将其number初始化为1,并初始化创建时间
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne=shoppingCart;
        }

        return R.success(shoppingCart);
    }

    /**
     * 将购物车内某一菜品/套餐数量减一*
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("删除购物车数据:{}",shoppingCart.toString());

        //将查找范围缩小到当前用户
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        lambdaQueryWrapper.eq(BaseContext.getCurrentId()!=null,ShoppingCart::getUserId,BaseContext.getCurrentId());

        //判断要删除的是菜品还是套餐
        if(shoppingCart.getDishId()!=null){
            //要删除的是菜品
            lambdaQueryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());

        }else{
            //要删除的是套餐
            lambdaQueryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        shoppingCart=shoppingCartService.getOne(lambdaQueryWrapper);

        if(shoppingCart!=null){
            //将当前套餐/菜品数量减一
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            shoppingCartService.updateById(shoppingCart);

            //当前套餐/菜品number为0时则删除当前套餐/菜品
            if(shoppingCart.getNumber()==0){
                shoppingCartService.removeById(shoppingCart);
            }
        }


        return R.success(shoppingCart);
    }

    /**
     * 清空购物车*
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据当前用户id,将当前用户的购物车清空
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<ShoppingCart>();
        lambdaQueryWrapper.eq(userId!=null,ShoppingCart::getUserId,userId);

        shoppingCartService.remove(lambdaQueryWrapper);

        return R.success("清空购物车成功！");
    }
}
