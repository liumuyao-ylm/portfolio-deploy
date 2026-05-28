package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.PurchaseItem;
import com.pharmacy.mapper.PurchaseItemMapper;
import com.pharmacy.service.PurchaseItemService;
import org.springframework.stereotype.Service;

@Service
public class PurchaseItemServiceImpl extends ServiceImpl<PurchaseItemMapper, PurchaseItem> implements PurchaseItemService {
}
