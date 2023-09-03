package com.hnn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnn.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
