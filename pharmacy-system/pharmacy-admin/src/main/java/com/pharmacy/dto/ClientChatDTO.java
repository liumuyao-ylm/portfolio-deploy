package com.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientChatDTO {
    @NotBlank(message = "咨询内容不能为空")
    @Size(max = 300, message = "咨询内容不能超过300字")
    private String message;
}
