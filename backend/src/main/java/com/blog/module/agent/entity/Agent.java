package com.blog.module.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("agent")
public class Agent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String avatar;
    private Long userId;
    private String capabilityDesc;
    private String provider;
    private String baseUrl;
    private String apiKeyEncrypted;
    private String modelName;
    private BigDecimal temperature;
    private Integer maxTokens;
    private Integer contextLength;
    private Integer isEnabled;
    private Integer rateLimitPerMin;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
