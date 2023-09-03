package com.hnn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnn.dto.SetmealDto;
import com.hnn.entity.Setmeal;

public interface ISetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时要保持与菜品的关联方式*
     * @param setmealDto
     */
    public void saveSetmealWithDish(SetmealDto setmealDto);

    /**
     * 修改套餐，并修改套餐内对应菜品*
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐--将套餐的isDeleted字段改为1*
     * @param id
     */
    public void deleteById(Long id);
}
