package com.blog.module.agent.service;

import com.blog.module.agent.dto.AgentSaveDTO;
import com.blog.module.agent.vo.AgentVO;

import java.util.List;

public interface AgentService {
    List<AgentVO> listEnabled();
    AgentVO getById(Long id);
    List<AgentVO> listAllAdmin();
    AgentVO getAdminById(Long id);
    void create(AgentSaveDTO dto);
    void update(Long id, AgentSaveDTO dto);
    void delete(Long id);
    void toggleEnabled(Long id);
}
