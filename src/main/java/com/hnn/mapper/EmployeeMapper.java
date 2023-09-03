package com.hnn.mapper;

import com.hnn.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 员工信息 Mapper 接口
 * </p>
 *
 * @author hnn
 * @since 2023-02-04
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
