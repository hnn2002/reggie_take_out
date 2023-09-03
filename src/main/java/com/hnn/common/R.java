package com.hnn.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> {
    //编码：1成功    0和其他数字表示失败
    private Integer code;

    //错误信息
    private String msg;

    //数据
    private T data;

    //动态数据
    private Map map=new HashMap();

    //成功函数
    /*定义泛型方法时，必须在返回值前加一个<T>，然后才可以用泛型T作为方法的返回值*/
    public static <T>R<T> success(T object){
        R<T> r=new R<T>();
        r.data=object;
        r.code=1;
        return r;
    }

    //错误函数
    public static <T>R<T> error(String msg){
        R<T> r=new R<T>();
        r.msg=msg;
        r.code=0;
        return r;
    }

    //操作动态数据
    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
