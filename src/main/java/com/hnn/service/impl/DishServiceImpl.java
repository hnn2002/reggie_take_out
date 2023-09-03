package com.hnn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.dto.DishDto;
import com.hnn.entity.Dish;
import com.hnn.entity.DishFlavor;
import com.hnn.mapper.DishMapper;
import com.hnn.service.IDishFlavorService;
import com.hnn.service.IDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Autowired
    IDishFlavorService dishFlavorService;


    /**
     * 新增菜品,同时插入菜品对应的口味数据，需要操作两张表dish，dish_flavor*
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表
        this.save(dishDto);

        Long id = dishDto.getId();

        //保存菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味到菜品口味表
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 修改菜品,同时修改菜品对应的口味数据，需要操作两张表dish，dish_flavor*
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<DishFlavor>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);


        //为口味数据设置对应的dishId
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors  = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //添加当前提交过来的口味数据--dish_flavor表的insert操作
        dishFlavorService.saveBatch(flavors);


    }

    /**
     * 根据菜品id 删除菜品*
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        //根据菜品id查询对应菜品
        Dish dish = this.getById(id);

        //将菜品的id_delete置为1
        dish.setIsDeleted(1);

        //更新菜品
        this.updateById(dish);
    }
}
