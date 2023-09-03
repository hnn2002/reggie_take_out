package com.hnn.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 员工信息
 * </p>
 *
 * @author hnn
 * @since 2023-02-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("employee")
@ApiModel(value="Employee对象", description="员工信息")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @ApiModelProperty(value = "状态 0:禁用，1:正常")
    private Integer status;

    @TableField(fill= FieldFill.INSERT)//插入时填充字段
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill=FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @TableField(fill=FieldFill.INSERT)//插入时填充字段
    @ApiModelProperty(value = "创建人")
    private Long createUser;

    @TableField(fill=FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    @ApiModelProperty(value = "修改人")
    private Long updateUser;


}
