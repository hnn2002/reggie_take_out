package com.hnn.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AddressBook implements Serializable {

    private static final Long serialVersionUID = 1L;

    //主键
    private Long id;

    //用户id
    private Long userId;

    //收货人
    private String consignee;

    //性别 0--女  1---男
    private String sex;

    //手机号
    private String phone;

    //省级区划编号
    private String provinceCode;

    //省级名称
    private String provinceName;

    //市级区划编号
    private String cityCode;

    //市级名称
    private String cityName;

    //区级区划编号
    private String districtCode;

    //区级名称
    private String districtName;

    //详细地址
    private String detail;

    //标签
    private String label;

    //默认 0 否 1是
    private Integer isDefault;

    //创建时间
    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime createTime;

    //更新时间
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //创建人
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    //修改人
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Long updateUser;

    //是否删除
    private Integer isDeleted;

   /* //对is_default字段单独处理
    public void setIsDefault(Integer isDefault){
        if(isDefault==1){
            this.isDefault=true;
        }else{
            this.isDefault=false;
        }
    }

    public Integer getIsDefault(){
        if(this.isDefault==true){
            return 1;
        }else{
            return 0;
        }

    }

    //对sex字段单独处理
    public void setSex(Integer sex){
        if(sex==1){
            this.sex=true;
        }else{
            this.sex=false;
        }
    }

    public Integer getSex(){
        if(this.sex==true){
            return 1;
        }else{
            return 0;
        }

    }*/
}
