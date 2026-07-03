package com.blog.module.agent.controller;

import com.blog.common.Result;
import com.blog.module.agent.dto.AgentChatDTO;
import com.blog.module.agent.service.AgentChatService;
import com.blog.module.agent.service.AgentService;
import com.blog.module.agent.vo.AgentChatVO;
import com.blog.module.agent.vo.AgentMessageVO;
import com.blog.module.agent.vo.AgentVO;
import com.blog.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;
    private final AgentChatService agentChatService;

    @GetMapping
    public Result<List<AgentVO>> listEnabled() {
        Long userId = UserContext.getUserId();
        return Result.success(agentService.listEnabled(userId));
    }

    @GetMapping("/{id}")
    public Result<AgentVO> getById(@PathVariable Long id) {
        return Result.success(agentService.getById(id));
    }

    @PostMapping("/{id}/chat")
    public Result<AgentChatVO> chat(@PathVariable Long id,
                                    @Valid @RequestBody AgentChatDTO dto,
                                    HttpServletRequest request) {
        return Result.success(agentChatService.chat(id, dto.getSessionId(), dto.getMessage(), request));
    }

    @GetMapping("/{id}/chat/{sessionId}")
    public Result<List<AgentMessageVO>> getHistory(@PathVariable Long id,
                                                    @PathVariable String sessionId) {
        return Result.success(agentChatService.getHistory(id, sessionId));
    }

    @DeleteMapping("/{id}/chat/{sessionId}")
    public Result<?> clearHistory(@PathVariable Long id,
                                  @PathVariable String sessionId) {
        agentChatService.clearHistory(id, sessionId);
        return Result.success();
    }
}
