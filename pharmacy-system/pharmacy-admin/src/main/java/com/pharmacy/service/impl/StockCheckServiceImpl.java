package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.StockCheck;
import com.pharmacy.mapper.StockCheckMapper;
import com.pharmacy.service.StockCheckService;
import org.springframework.stereotype.Service;

@Service
public class StockCheckServiceImpl extends ServiceImpl<StockCheckMapper, StockCheck> implements StockCheckService {
}
