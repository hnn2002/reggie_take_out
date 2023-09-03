package com.hnn.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnn.common.R;
import com.hnn.dto.DishDto;
import com.hnn.entity.Category;
import com.hnn.entity.Dish;
import com.hnn.entity.DishFlavor;
import com.hnn.service.ICategoryService;
import com.hnn.service.IDishFlavorService;
import com.hnn.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private IDishService dishService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IDishFlavorService dishFlavorService;

    /**
     * 新增菜品*
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("新增菜品：{}",dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 查询*
     * @param currentPage
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam(value="page") int currentPage,@RequestParam(value="pageSize") int pageSize,@RequestParam(value="name",required = false) String name){
        log.info("currentPage={}，pageSize={}",currentPage,pageSize);

        //创建page对象
        Page<Dish> dishPage=new Page<Dish>(currentPage,pageSize);
        Page<DishDto> dishDtoPage=new Page<DishDto>();


        //构造条件查询器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<Dish>();
        //按照更新时间排序
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //输入菜品名称查询菜品
        lambdaQueryWrapper.like(Strings.isNotEmpty(name),Dish::getName,name);

        //进行分页查询
        dishService.page(dishPage, lambdaQueryWrapper);

        //对象拷贝(records要单独处理)
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        //单独处理records
        List<Dish> dishPageRecords = dishPage.getRecords();

        //将创建的DishDto对象组成集合
        List<DishDto> dishDtoPageResords = dishPageRecords.stream().map((item) -> {
            //新建一个dishDto对象
            DishDto dishDto = new DishDto();

            //将item的属性赋值到dishDto对象上
            BeanUtils.copyProperties(item, dishDto);

            //categoryId查找对应的categoryName
            Category category = categoryService.getById(item.getCategoryId());

            if(category!=null){
                //设置dishDto对象的categoryName属性值
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());

        //设置dishDtoPage对象的Records属性
        dishDtoPage.setRecords(dishDtoPageResords);

        return R.success(dishDtoPage);
    }

    /**
     * 根据菜品id获取对应的菜品和菜品口味  *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishWithFlavorById(@PathVariable Long id){
        log.info("getDishWithFlavorById()，id={}",id);

        //创建一个dishDto对象
        DishDto dishDto=new DishDto();

        //根据id获取dish对象
        Dish dish = dishService.getById(id);

        //将dish对象的属性拷贝到dishDto对象上
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品口味相关信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<DishFlavor>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

        //设置菜品口味相关信息
        dishDto.setFlavors(dishFlavorList);

        return R.success(dishDto);
    }

    /**
     * 修改菜品*
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("新增菜品：{}",dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /**
     * 删除菜品*
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam(value="ids") Long[] id){
        log.info("删除dish，dishId={}",id.toString());

        for (Long aLong : id) {
            dishService.deleteById(aLong);
        }

        return R.success("删除成功");
    }

    /**
     *修改售卖状态*
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable Integer status,@RequestParam(value="ids") Long[] id){
        for (Long aLong : id) {
            Dish dish = dishService.getById(aLong);
            dish.setStatus(status);
            dishService.updateById(dish);
        }

        return R.success("修改成功");
    }

    /**
     * 查询所有菜品*
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造条件查询
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<Dish>();

        //添加条件，查询状态为1的（起售状态）
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //按条件查询（CategoryId）
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //按条件查询（name）
        lambdaQueryWrapper.like(Strings.isNotEmpty(dish.getName()),Dish::getName,dish.getName());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        //开始查询
        List<Dish> dishList = dishService.list(lambdaQueryWrapper);

        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            //新建一个dishDto对象
            DishDto dishDto = new DishDto();

            //将dish的属性复制给dishDto
            BeanUtils.copyProperties(item, dishDto);

            //根据categoryId获取对应的categoryName
            Category category = categoryService.getById(dishDto.getCategoryId());
            String categoryName = category.getName();

            //设置dishDto对象的categoryName属性
            dishDto.setCategoryName(categoryName);

            //根据dishDto的id对应的菜品口味数据
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<DishFlavor>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
            dishFlavorLambdaQueryWrapper.orderByDesc(DishFlavor::getUpdateTime);
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            //设置dishDto对象的flavors属性
            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }
}
