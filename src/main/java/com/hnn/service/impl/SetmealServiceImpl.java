package com.hnn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.dto.SetmealDto;
import com.hnn.entity.Setmeal;
import com.hnn.entity.SetmealDish;
import com.hnn.mapper.SetmealMapper;
import com.hnn.service.ISetmealDishService;
import com.hnn.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService setmealDishService;

    /**
     * 新增套餐，同时要保持与菜品的关联方式*
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveSetmealWithDish(SetmealDto setmealDto) {
        //保存setmealDto中的setmeal字段
        this.save(setmealDto);

        //获取setmealDto中的setmeal_dish集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //保存setmealDish对象
        setmealDishes= setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 修改套餐信息，并修改套餐内对应的菜品*
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal基本表
        this.updateById(setmealDto);

        //清理当前套餐对应菜品数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<SetmealDish>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);

        //为菜品数据设置当前的setmealId
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //添加当前提交过来的套餐对应菜品数据
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐信息--将套餐的isDeleted字段变为1*
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        Setmeal setmeal = this.getById(id);
        setmeal.setIsDeleted(1);
        this.updateById(setmeal);
    }
}
