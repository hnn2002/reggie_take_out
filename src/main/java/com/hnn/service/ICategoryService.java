package com.hnn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnn.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;

public interface ICategoryService extends IService<Category> {

    //根据id删除分类
    public void remove(Long id);


}
