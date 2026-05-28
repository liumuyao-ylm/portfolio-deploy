package com.pharmacy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.entity.Stock;
import com.pharmacy.vo.StockVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {
    
    @Select("SELECT s.*, m.med_name, COALESCE(sp.short_name, sp.supplier_name) AS supplier_name, m.spec, m.unit " +
            "FROM stock s LEFT JOIN medicine m ON s.med_id = m.med_id " +
            "LEFT JOIN supplier sp ON s.supplier_id = sp.supplier_id " +
            "ORDER BY s.create_time DESC")
    IPage<StockVO> selectStockPageWithMedicine(Page<StockVO> page);

    @Select("SELECT s.*, m.med_name, COALESCE(sp.short_name, sp.supplier_name) AS supplier_name, m.spec, m.unit " +
            "FROM stock s LEFT JOIN medicine m ON s.med_id = m.med_id " +
            "LEFT JOIN supplier sp ON s.supplier_id = sp.supplier_id " +
            "ORDER BY s.create_time DESC")
    List<StockVO> selectStockListWithMedicine();

    @Select("SELECT s.*, m.med_name, COALESCE(sp.short_name, sp.supplier_name) AS supplier_name, m.spec, m.unit " +
            "FROM stock s LEFT JOIN medicine m ON s.med_id = m.med_id " +
            "LEFT JOIN supplier sp ON s.supplier_id = sp.supplier_id " +
            "WHERE s.expire_date < #{today} AND s.status = 1 " +
            "ORDER BY s.expire_date ASC, s.create_time DESC " +
            "LIMIT 5")
    List<StockVO> selectExpiredStockReminders(@Param("today") LocalDate today);
}
