package com.hnn.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnn.common.R;
import com.hnn.entity.Category;
import com.hnn.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;

    /*分页查询*/
    @GetMapping("/page")
    public R<Page> page(@RequestParam(value = "page") Integer currentPage, @RequestParam(value="pageSize") Integer pageSize){
        log.info("currentPage={}，pageSize={}",currentPage,pageSize);
        //添加分页构造器
        Page page=new Page(currentPage,pageSize);
        //构造条件选择器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<Category>();
        //按照sort排序
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //进行分页查询
        categoryService.page(page,lambdaQueryWrapper);

        return R.success(page);
    }

    //新增菜品（套餐）分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增category:{}",category.toString());
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    //根据id修改分类
    @PutMapping
    public R<String> updateById(@RequestBody Category category){
        log.info("修改分类，id为：",category.getId());
        categoryService.updateById(category);
        return R.success("分类修改成功");
    }

    //根据id删除分类
    @DeleteMapping
    public R<String> deleteById(@RequestParam(value="ids") Long id){
        log.info("删除分类，id为：{}",id);

        //categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    //获取菜品分类
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("获取菜品分类：{}",category.toString());

        //构造条件查询器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<Category>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }
}
