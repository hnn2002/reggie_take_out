package com.hnn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnn.dto.DishDto;
import com.hnn.entity.Dish;

public interface IDishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据，需要操作两张表dish，dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //修改菜品,同时修改菜品对应的口味数据，需要操作两张表dish，dish_flavor
    public void updateWithFlavor(DishDto dishDto);

    //根据菜品id 删除菜品
    public void deleteById(Long id);
}
