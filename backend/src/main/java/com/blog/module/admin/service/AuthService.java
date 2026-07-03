package com.blog.module.admin.service;

import com.blog.module.admin.dto.AdminInfoDTO;
import com.blog.module.admin.dto.LoginDTO;
import com.blog.module.admin.dto.PasswordChangeDTO;
import com.blog.module.admin.vo.AdminVO;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginDTO loginDTO);
    void logout(Long adminId);
    AdminVO getProfile(Long adminId);
    void updateProfile(Long adminId, AdminInfoDTO dto);
    void changePassword(Long adminId, PasswordChangeDTO dto);
}
