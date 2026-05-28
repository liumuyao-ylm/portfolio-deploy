package com.pharmacy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pharmacy.entity.Stock;
import com.pharmacy.mapper.StockMapper;
import com.pharmacy.service.StockService;
import com.pharmacy.vo.StockVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {
    @Override
    public IPage<StockVO> getStockPageWithMedicine(Page<StockVO> page) {
        IPage<StockVO> stockPage = baseMapper.selectStockPageWithMedicine(page);
        LocalDate today = LocalDate.now();

        for (StockVO vo : stockPage.getRecords()) {
            fillStockFlags(vo, today);
        }

        return stockPage;
    }

    @Override
    public List<StockVO> listStockWithMedicine() {
        List<StockVO> list = baseMapper.selectStockListWithMedicine();
        LocalDate today = LocalDate.now();
        list.forEach(vo -> fillStockFlags(vo, today));
        return list;
    }

    private void fillStockFlags(StockVO vo, LocalDate today) {
        if (vo.getExpireDate() != null) {
            long daysUntilExpire = ChronoUnit.DAYS.between(today, vo.getExpireDate());
            vo.setIsExpired(daysUntilExpire < 0);
            vo.setIsNearExpire(daysUntilExpire >= 0 && daysUntilExpire <= 30);
        } else {
            vo.setIsExpired(false);
            vo.setIsNearExpire(false);
        }

        if (vo.getStockMin() != null && vo.getStockNum() != null) {
            vo.setIsLowStock(vo.getStockNum() <= vo.getStockMin());
        } else {
            vo.setIsLowStock(false);
        }
    }
}
