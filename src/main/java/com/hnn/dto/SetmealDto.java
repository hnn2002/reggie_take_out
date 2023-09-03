package com.hnn.dto;

import com.hnn.entity.Setmeal;
import com.hnn.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
