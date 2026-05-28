package com.pharmacy.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockCheckCreateDTO {
    private String checkNo;
    private Integer checkUser;
    private LocalDateTime checkTime;
    private String remark;
    private List<StockCheckItemDTO> items;

    @Data
    public static class StockCheckItemDTO {
        private Integer medId;
        private String batchNo;
        private Integer systemStock;
        private Integer actualStock;
        private BigDecimal unitPrice;
        private String reason;
        private String remark;
    }
}
