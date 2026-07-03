package com.blog.module.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("agent_conversation")
public class AgentConversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long agentId;
    private String sessionId;
    private String role;
    private String content;
    private String ipAddress;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
