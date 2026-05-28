package com.pharmacy.controller;

import com.pharmacy.common.Result;
import com.pharmacy.dto.ClientChatDTO;
import com.pharmacy.vo.ClientChatVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/client")
public class ClientChatController {

    private static final String SAFETY_REPLY = "我可以提供药店服务和药品查询相关帮助，但不能替代医生或药师诊断。若涉及具体病情、用药剂量、联合用药或不适症状，请咨询店内药师或及时就医。";
    private static final String FALLBACK_REPLY = "我暂时只能回答营业时间、地址电话、处方药购买、会员记录和药品查询等药店服务问题。您也可以拨打 0312-6688120 咨询店内工作人员。";

    @PostMapping("/chat")
    public Result<ClientChatVO> chat(@Valid @RequestBody ClientChatDTO dto) {
        String message = normalize(dto.getMessage());
        return Result.success(new ClientChatVO(matchReply(message)));
    }

    private String normalize(String message) {
        return message == null ? "" : message.trim().toLowerCase(Locale.ROOT);
    }

    private String matchReply(String message) {
        if (containsAny(message, "感冒", "发烧", "咳嗽", "头疼", "胃疼", "腹泻", "过敏", "症状", "诊断", "剂量", "用量", "吃什么药", "怎么吃", "联合用药")) {
            return SAFETY_REPLY;
        }
        if (containsAny(message, "营业", "几点", "时间", "开门", "关门")) {
            return "本店营业时间为 08:00 - 21:30，全年无休。到店前如需确认药师是否在岗，可先拨打 0312-6688120。";
        }
        if (containsAny(message, "地址", "在哪", "哪里", "位置", "路线", "导航")) {
            return "本店地址为河北省保定市清苑区安心镇中心街 26 号，靠近镇卫生院和便民超市。您可以在药店信息页查看路线说明。";
        }
        if (containsAny(message, "电话", "联系", "号码", "客服")) {
            return "药店联系电话为 0312-6688120。处方药、库存和到店服务都可以先电话确认。";
        }
        if (containsAny(message, "处方", "处方药", "凭处方")) {
            return "处方药需要凭有效处方购买。请携带医生开具的处方到店，由药师核验后协助购药。";
        }
        if (containsAny(message, "会员", "消费记录", "订单", "积分", "记录")) {
            return "会员资料和消费记录可以在用户端的会员中心查看。进入会员中心后，系统会展示会员信息和最近消费记录。";
        }
        if (containsAny(message, "药品", "库存", "有货", "还有货", "价格", "查询", "布洛芬")) {
            return "您可以进入药品查询页按药品名称、分类、剂型或库存状态检索。页面展示的库存和价格以系统实时查询结果为准。";
        }
        return FALLBACK_REPLY;
    }

    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
