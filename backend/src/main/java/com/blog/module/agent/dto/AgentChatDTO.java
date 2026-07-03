package com.blog.module.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentChatDTO {
    private String sessionId;
    @NotBlank(message = "消息不能为空")
    private String message;
}
