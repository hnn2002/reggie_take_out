package com.hnn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnn.entity.AddressBook;
import com.hnn.mapper.AddressBookMapper;
import com.hnn.service.IAddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {
}
