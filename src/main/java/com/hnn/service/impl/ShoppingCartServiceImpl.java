package com.hnn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.entity.ShoppingCart;
import com.hnn.mapper.ShoppingCartMapper;
import com.hnn.service.IShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
}
