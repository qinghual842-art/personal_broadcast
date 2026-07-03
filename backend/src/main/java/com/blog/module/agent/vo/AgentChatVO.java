package com.blog.module.agent.vo;

import lombok.Data;

import java.util.List;

@Data
public class AgentChatVO {
    private String sessionId;
    private String reply;
    private List<AgentMessageVO> history;
}
