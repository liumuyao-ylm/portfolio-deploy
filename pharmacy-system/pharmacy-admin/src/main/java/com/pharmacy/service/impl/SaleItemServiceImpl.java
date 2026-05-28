package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.mapper.SaleItemMapper;
import com.pharmacy.service.SaleItemService;
import org.springframework.stereotype.Service;

@Service
public class SaleItemServiceImpl extends ServiceImpl<SaleItemMapper, SaleItem> implements SaleItemService {
}
