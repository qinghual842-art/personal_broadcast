package com.blog.module.agent.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AgentVO {
    private Long id;
    private String name;
    private String description;
    private String avatar;
    private String capabilityDesc;
    private String provider;
    private String baseUrl;
    private String modelName;
    private BigDecimal temperature;
    private Integer maxTokens;
    private Integer contextLength;
    private Integer isEnabled;
    private Integer rateLimitPerMin;
    private Long userId;
    private LocalDateTime createTime;
}
