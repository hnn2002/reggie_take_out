package com.hnn.service.impl;

import com.hnn.entity.Employee;
import com.hnn.mapper.EmployeeMapper;
import com.hnn.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author hnn
 * @since 2023-02-04
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

}
