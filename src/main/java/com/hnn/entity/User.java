package com.hnn.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final Long serialVersionUID = 1l;

    //主键
    private Long id;

    //姓名
    private String name;

    //手机号
    private String phone;

    //性别
    private String sex;

    //身份证号
    private String idNumber;

    //头像
    private String avatar;

    //状态 0-禁用  1-正常
    private Integer status;
}
