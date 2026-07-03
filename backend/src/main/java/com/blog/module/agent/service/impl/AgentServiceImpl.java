package com.blog.module.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.exception.BusinessException;
import com.blog.module.agent.dto.AgentSaveDTO;
import com.blog.module.agent.entity.Agent;
import com.blog.module.agent.mapper.AgentMapper;
import com.blog.module.agent.service.AgentService;
import com.blog.module.agent.vo.AgentVO;
import com.blog.util.AesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentMapper agentMapper;
    private final AesUtil aesUtil;

    @Override
    public List<AgentVO> listEnabled(Long userId) {
        List<Agent> list = agentMapper.selectList(
                new LambdaQueryWrapper<Agent>()
                        .eq(Agent::getIsEnabled, 1)
                        .and(w -> w.isNull(Agent::getUserId).or().eq(Agent::getUserId, userId))
                        .orderByDesc(Agent::getCreateTime));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    public List<AgentVO> listAllAdmin() {
        List<Agent> list = agentMapper.selectList(
                new LambdaQueryWrapper<Agent>()
                        .orderByDesc(Agent::getCreateTime));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    public AgentVO getById(Long id) {
        if (id == null) {
            throw new BusinessException(400, "智能体ID不能为空");
        }
        Agent agent = agentMapper.selectById(id);
        if (agent == null) {
            throw new BusinessException(404, "智能体不存在");
        }
        return toVO(agent);
    }

    @Override
    public AgentVO getAdminById(Long id) {
        if (id == null) {
            throw new BusinessException(400, "智能体ID不能为空");
        }
        Agent agent = agentMapper.selectById(id);
        if (agent == null) {
            throw new BusinessException(404, "智能体不存在");
        }
        return toVO(agent);
    }

    @Override
    @Transactional
    public void create(AgentSaveDTO dto) {
        if (dto == null || isBlank(dto.getName())) {
            throw new BusinessException(400, "智能体名称不能为空");
        }
        if (isBlank(dto.getProvider())) {
            throw new BusinessException(400, "提供商不能为空");
        }
        if (isBlank(dto.getApiKey())) {
            throw new BusinessException(400, "API Key不能为空");
        }
        if (isBlank(dto.getModelName())) {
            throw new BusinessException(400, "模型名称不能为空");
        }

        Agent agent = new Agent();
        BeanUtils.copyProperties(dto, agent, "apiKey", "id", "createTime", "updateTime");
        agent.setApiKeyEncrypted(aesUtil.encrypt(dto.getApiKey()));
        if (agent.getIsEnabled() == null) {
            agent.setIsEnabled(0);
        }
        if (agent.getRateLimitPerMin() == null) {
            agent.setRateLimitPerMin(10);
        }
        if (agent.getContextLength() == null) {
            agent.setContextLength(10);
        }
        agentMapper.insert(agent);
    }

    @Override
    @Transactional
    public void update(Long id, AgentSaveDTO dto) {
        if (id == null) {
            throw new BusinessException(400, "智能体ID不能为空");
        }
        if (dto == null || isBlank(dto.getName())) {
            throw new BusinessException(400, "智能体名称不能为空");
        }

        Agent agent = agentMapper.selectById(id);
        if (agent == null) {
            throw new BusinessException(404, "智能体不存在");
        }

        BeanUtils.copyProperties(dto, agent, "id", "apiKey", "apiKeyEncrypted", "createTime");

        // Only re-encrypt if a new API key is provided
        if (dto.getApiKey() != null && !dto.getApiKey().isBlank()) {
            agent.setApiKeyEncrypted(aesUtil.encrypt(dto.getApiKey()));
        }

        agent.setId(id);
        agentMapper.updateById(agent);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(400, "智能体ID不能为空");
        }
        int deleted = agentMapper.deleteById(id);
        if (deleted == 0) {
            throw new BusinessException(404, "智能体不存在");
        }
    }

    @Override
    @Transactional
    public void toggleEnabled(Long id) {
        if (id == null) {
            throw new BusinessException(400, "智能体ID不能为空");
        }
        Agent agent = agentMapper.selectById(id);
        if (agent == null) {
            throw new BusinessException(404, "智能体不存在");
        }
        agent.setIsEnabled(agent.getIsEnabled() != null && agent.getIsEnabled() == 1 ? 0 : 1);
        agentMapper.updateById(agent);
    }

    // ─── User Agent APIs ────────────────────────────────────────

    @Override
    public List<AgentVO> listUserAgents(Long userId) {
        if (userId == null) return List.of();
        List<Agent> list = agentMapper.selectList(
                new LambdaQueryWrapper<Agent>()
                        .eq(Agent::getUserId, userId)
                        .orderByDesc(Agent::getCreateTime));
        return list.stream().map(this::toVO).toList();
    }

    @Override
    @Transactional
    public void createUserAgent(AgentSaveDTO dto, Long userId) {
        if (dto == null || isBlank(dto.getName())) {
            throw new BusinessException(400, "智能体名称不能为空");
        }
        if (isBlank(dto.getProvider())) {
            throw new BusinessException(400, "请选择大模型提供商");
        }
        if (isBlank(dto.getApiKey())) {
            throw new BusinessException(400, "请输入API密钥");
        }
        if (isBlank(dto.getModelName())) {
            throw new BusinessException(400, "请输入模型名称");
        }
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Agent agent = new Agent();
        BeanUtils.copyProperties(dto, agent, "apiKey", "id", "createTime", "updateTime");
        agent.setUserId(userId);
        agent.setApiKeyEncrypted(aesUtil.encrypt(dto.getApiKey()));
        if (agent.getIsEnabled() == null) agent.setIsEnabled(1);
        if (agent.getRateLimitPerMin() == null) agent.setRateLimitPerMin(10);
        if (agent.getContextLength() == null) agent.setContextLength(10);
        agentMapper.insert(agent);
    }

    @Override
    @Transactional
    public void updateUserAgent(Long id, AgentSaveDTO dto, Long userId) {
        if (id == null) throw new BusinessException(400, "智能体ID不能为空");
        if (dto == null || isBlank(dto.getName())) throw new BusinessException(400, "智能体名称不能为空");

        Agent agent = agentMapper.selectById(id);
        if (agent == null) throw new BusinessException(404, "智能体不存在");
        if (!userId.equals(agent.getUserId())) throw new BusinessException(403, "无权修改此智能体");

        BeanUtils.copyProperties(dto, agent, "id", "apiKey", "apiKeyEncrypted", "userId", "createTime");
        if (dto.getApiKey() != null && !dto.getApiKey().isBlank()) {
            agent.setApiKeyEncrypted(aesUtil.encrypt(dto.getApiKey()));
        }
        agent.setId(id);
        agentMapper.updateById(agent);
    }

    @Override
    @Transactional
    public void deleteUserAgent(Long id, Long userId) {
        if (id == null) throw new BusinessException(400, "智能体ID不能为空");
        Agent agent = agentMapper.selectById(id);
        if (agent == null) throw new BusinessException(404, "智能体不存在");
        if (!userId.equals(agent.getUserId())) throw new BusinessException(403, "无权删除此智能体");
        agentMapper.deleteById(id);
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private AgentVO toVO(Agent agent) {
        AgentVO vo = new AgentVO();
        BeanUtils.copyProperties(agent, vo);
        return vo;
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
