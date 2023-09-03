package com.hnn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.entity.User;
import com.hnn.mapper.UserMapper;
import com.hnn.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
}
