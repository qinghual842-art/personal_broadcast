package com.blog.module.agent.service;

import com.blog.module.agent.vo.AgentChatVO;
import com.blog.module.agent.vo.AgentMessageVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AgentChatService {
    AgentChatVO chat(Long agentId, String sessionId, String message, HttpServletRequest request);
    List<AgentMessageVO> getHistory(Long agentId, String sessionId);
    void clearHistory(Long agentId, String sessionId);
}
