package com.blog.module.agent.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.module.agent.dto.AgentSaveDTO;
import com.blog.module.agent.service.AgentService;
import com.blog.module.agent.vo.AgentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/agents")
@RequiredArgsConstructor
public class AgentAdminController {

    private final AgentService agentService;

    @GetMapping
    public Result<List<AgentVO>> listAll() {
        return Result.success(agentService.listAllAdmin());
    }

    @PostMapping
    public Result<?> create(@Valid @RequestBody AgentSaveDTO dto) {
        agentService.create(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AgentVO> getById(@PathVariable Long id) {
        return Result.success(agentService.getAdminById(id));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody AgentSaveDTO dto) {
        agentService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        agentService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    public Result<?> toggleEnabled(@PathVariable Long id) {
        agentService.toggleEnabled(id);
        return Result.success();
    }
}
