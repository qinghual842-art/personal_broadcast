package com.blog.module.agent.vo;

import lombok.Data;

@Data
public class AgentMessageVO {
    private String role;
    private String content;
    private String timestamp;
}
