package com.hnn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.entity.DishFlavor;
import com.hnn.mapper.DishFlavorMapper;
import com.hnn.service.IDishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements IDishFlavorService {
}
