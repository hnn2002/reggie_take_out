package com.hnn.controller;

import com.alibaba.fastjson.serializer.AdderSerializer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hnn.common.BaseContext;
import com.hnn.common.R;
import com.hnn.entity.AddressBook;
import com.hnn.service.IAddressBookService;
import com.sun.jndi.cosnaming.IiopUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private IAddressBookService addressBookService;

    /**
     * 获取当前用户所有地址*
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        //获取当前用户的id
        Long currentId = BaseContext.getCurrentId();
        log.info("当前用户id={}",currentId);

        //构造条件查询器，根据当前用户id查询对应收货地址
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<AddressBook>();
        lambdaQueryWrapper.eq(currentId!=null,AddressBook::getUserId,currentId);
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);

         return R.success(list);
    }

    /**
     * 设置为默认的收获地址，默认的收获地址最多有一个*
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        log.info("设置默认地址：{}",addressBook.toString());

        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();

        //根据当前用户的id获取当前用户所有的收货地址，并将当前用户所有收货地址的is_default字段置为0
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper=new LambdaUpdateWrapper<AddressBook>();
        lambdaUpdateWrapper.eq(userId!=null,AddressBook::getUserId,userId);
        lambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(lambdaUpdateWrapper);

        //获取前端传过来的收货地址的id
        Long id = addressBook.getId();

        //根据收货id查询对应收货地址
        addressBook.setIsDefault(1);

        //将查询到的收获地址对应的is_default字段改成1
        addressBookService.updateById(addressBook);

        return R.success("设置默认地址成功");
    }

    /**
     * 保存当前收货地址*
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        log.info("save:{}",addressBook.toString());

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);

        //保存收货地址
        addressBookService.save(addressBook);

        return R.success("收货地址保存成功");
    }

    /**
     * 根据指定收货地址id查询对应收货地址*
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        log.info("当前收获地址id为:{}",id);

        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook!=null){
            return R.success(addressBook);
        }else{
            return R.error("没有找到该收获地址！");
        }

    }

    /**
     * 根据地址id更新收货地址*
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info("update:{}",addressBook.toString());

        addressBookService.updateById(addressBook);

        return R.success("修改成功！");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        log.info("获取默认收货地址...");

        //获取当前用户id
        Long userId = BaseContext.getCurrentId();

        //根据当前用户id查询address_book表中对应的值为1的is_default字段
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<AddressBook>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,userId);
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(lambdaQueryWrapper);

        //没有找到默认收货地址
        if(addressBook==null){
            return R.error("没有找到默认收货地址！");
        }

        return R.success(addressBook);
    }

}
