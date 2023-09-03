package com.hnn.dto;

import com.hnn.entity.Dish;
import com.hnn.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *用于封装页面提交的数据 *
 * DTO   （Data Transfer Object 用于展示层与服务层之间的数据传输）*
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors=new ArrayList<>();

    private String categoryName;

    private Integer copies;

}
