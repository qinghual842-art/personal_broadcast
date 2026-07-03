package com.blog.module.agent.controller;

import com.blog.common.Result;
import com.blog.module.agent.dto.AgentSaveDTO;
import com.blog.module.agent.service.AgentService;
import com.blog.module.agent.vo.AgentVO;
import com.blog.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/agents")
@RequiredArgsConstructor
public class UserAgentController {

    private final AgentService agentService;

    @GetMapping
    public Result<List<AgentVO>> list() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        return Result.success(agentService.listUserAgents(userId));
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody AgentSaveDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        agentService.createUserAgent(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody AgentSaveDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        agentService.updateUserAgent(id, dto, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        agentService.deleteUserAgent(id, userId);
        return Result.success();
    }
}
