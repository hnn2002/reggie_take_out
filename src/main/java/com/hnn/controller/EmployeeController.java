package com.hnn.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnn.common.BaseContext;
import com.hnn.common.R;
import com.hnn.entity.Employee;
import com.hnn.service.IEmployeeService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author hnn
 * @since 2023-02-04
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    /*员工登陆*/
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将密码进行md5加密处理
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名来查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<Employee>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);


        //3.如果没有查询到则返回失败结果
        if(emp==null){
            return R.error("查询失败");
        }

        //4.对比密码，不一致则返回失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //5.查看员工状态，如果已禁用状态，则返回员工已禁用
        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        //6.登陆成功，将用户id存入session
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    /*退出登录*/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /*添加员工*/
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息:{}",employee.toString());

        //设置初始化密码，此密码需要进行MD5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增用户成功");
    }

    /*分页查询*/
    @GetMapping("/page")
    public R<Page> page(@RequestParam(value = "page",required = true) int currentPage, @RequestParam(value="pageSize",required = true) int pageSize,@RequestParam(value="name" ,required = false) String name){

        log.info("currentPage={}，pageSize={}，name={}",currentPage,pageSize,name);
        //构造分页拦截器
        Page page=new Page(currentPage,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<Employee>();
        //添加过滤条件
        lambdaQueryWrapper.like(Strings.isNotEmpty(name),Employee::getName,name);
        //不展示管理员
        lambdaQueryWrapper.ne(Employee::getUsername,"admin");
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(page,lambdaQueryWrapper);

        return R.success(page);
    }

    /*修改员工状态*/
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("修改员工的信息：{}",employee.toString());

        //获取修改者的id
        Long id =(Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(id);
        //employee.setUpdateTime(LocalDateTime.now());

        //修改员工状态
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /*根据id查询员工信息*/
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查对象");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到该用户对应的信息");
    }
}
