package com.hnn.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnn.common.R;
import com.hnn.dto.SetmealDto;
import com.hnn.entity.Category;
import com.hnn.entity.Setmeal;
import com.hnn.entity.SetmealDish;
import com.hnn.service.ICategoryService;
import com.hnn.service.ISetmealDishService;
import com.hnn.service.ISetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private ISetmealService setmealService;

    @Autowired
    private ISetmealDishService setmealDishService;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 分页查询*
     * @param currentPage
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam(value="page") Integer currentPage,@RequestParam(value="pageSize") Integer pageSize,@RequestParam(value="name",required = false) String name){
        log.info("currentPage={}，pageSize={}",currentPage,pageSize);

        //新建page对象
        Page<Setmeal> setmealPage=new Page<Setmeal>(currentPage,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<SetmealDto>();

        //建立条件选择器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<Setmeal>();

        //根据name查询
        lambdaQueryWrapper.eq(Strings.isNotEmpty(name),Setmeal::getName,name);
        //排序
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //开始查询
        setmealService.page(setmealPage,lambdaQueryWrapper);

        //将setmealPage对象的属性拷贝到setmealDtoPage(records要单独处理)上
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        //单独处理records
        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> setmealDtoList = records.stream().map((item) -> {
            //新建一个SetmealDto对象
            SetmealDto setmealDto = new SetmealDto();

            //将item对象赋值给setmealDto对象
            BeanUtils.copyProperties(item, setmealDto);

            //根据对应categoryId查询对应的categoryName
            Category category = categoryService.getById(item.getCategoryId());

            if (category != null) {
                //设置setmealDto对象的categoryName属性
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //设置setmealDto的records属性
        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    /**
     * 新增套餐*
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐：{}",setmealDto.toString());

        setmealService.saveSetmealWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 根据套餐id查询对应套餐及套餐包含菜品*
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealWithDishById(@PathVariable Long id){
        log.info("getSetmealWithDishById，id={}",id);

        //创建一个SetmealDto对象
        SetmealDto setmealDto=new SetmealDto();

        //根据id获取setmeal对象
        Setmeal setmeal = setmealService.getById(id);

        //将setmeal对象的属性拷贝给setmealDto对象
        BeanUtils.copyProperties(setmeal,setmealDto);

        //根据setmealId查询符合条件的setmealDishs
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<SetmealDish>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);

        //设置semealDto对象的setmealDishes属性
        setmealDto.setSetmealDishes(setmealDishList);

        return R.success(setmealDto);
    }

    /**
     * 修改套餐，并修改套餐内对应的菜品*
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("update方法，{}",setmealDto.toString());

        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功");
    }

    /**
     * 修改套餐售卖状态*
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable Integer status,@RequestParam(value="ids") Long[] ids){
        log.info("修改套餐:{}",ids.toString());

        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }

    /**
     * 删除套餐信息--将套餐的isDeleted字段改为1*
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long[] ids){
        log.info("删除套餐:{}",ids.toString());

        for (Long id : ids) {
            setmealService.deleteById(id);
        }

        return R.success("删除成功");
    }

    /**
     * 根据categoryId查询对应的setmeal套餐数据*
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("list:{}",setmeal.toString());

        //根据categoryId查询对应的套餐数据
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<Setmeal>();
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(lambdaQueryWrapper);

        return R.success(setmealList);
    }
}
