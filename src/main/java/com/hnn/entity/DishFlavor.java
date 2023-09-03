package com.hnn.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    private Long id;

    //菜品
    private Long dishId;

    //口味名称
    private String name;

    //口味数据list
    private String value;

    //创建时间
    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime createTime;

    //更新时间
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //创建人
    @TableField(fill=FieldFill.INSERT)
    private Long createUser;

    //修改人
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Long updateUser;

    //是否删除   1--已删除  0--未删除
    private Integer isDeleted;


}
