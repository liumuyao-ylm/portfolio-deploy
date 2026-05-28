package com.pharmacy.controller;

import com.pharmacy.common.Result;
import com.pharmacy.vo.ClientStoreInfoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/client")
public class ClientStoreController {

    @GetMapping("/store-info")
    public Result<ClientStoreInfoVO> storeInfo() {
        ClientStoreInfoVO storeInfo = ClientStoreInfoVO.builder()
                .name("安心乡镇药房")
                .status("营业中")
                .hours("08:00 - 21:30")
                .phone("0312-6688120")
                .address("河北省保定市清苑区安心镇中心街 26 号")
                .scope("中成药、化学药制剂、抗生素制剂、生化药品、医疗器械、保健用品")
                .notice("处方药请凭处方购买，特殊用药建议先咨询店内药师。")
                .locationText("安心镇中心街")
                .routeText("沿安心镇中心街向东步行约 300 米，靠近镇卫生院和便民超市。")
                .services(Arrays.asList(
                        "提供常用药、慢病常备药、外伤消毒用品和基础医疗器械咨询。",
                        "处方药需凭有效处方购买，购药前可电话确认库存。",
                        "老人和慢病顾客可到店咨询药师用药注意事项。"
                ))
                .build();
        return Result.success(storeInfo);
    }
}
