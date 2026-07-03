package com.blog.module.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentSaveDTO {
    @NotBlank(message = "智能体名称不能为空")
    private String name;
    private String description;
    private String avatar;
    private String capabilityDesc;
    @NotBlank(message = "大模型提供商不能为空")
    private String provider;
    @NotBlank(message = "API密钥不能为空")
    private String apiKey;
    private String modelName;
    private BigDecimal temperature;
    private Integer maxTokens;
    private Integer contextLength;
    @NotNull(message = "请选择是否启用")
    private Integer isEnabled;
    private Integer rateLimitPerMin;
}
