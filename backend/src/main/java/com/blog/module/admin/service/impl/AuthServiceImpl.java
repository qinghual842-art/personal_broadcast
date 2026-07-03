package com.blog.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.exception.BusinessException;
import com.blog.module.admin.dto.AdminInfoDTO;
import com.blog.module.admin.dto.LoginDTO;
import com.blog.module.admin.dto.PasswordChangeDTO;
import com.blog.module.admin.entity.Admin;
import com.blog.module.admin.mapper.AdminMapper;
import com.blog.module.admin.service.AuthService;
import com.blog.module.admin.vo.AdminVO;
import com.blog.security.JwtTokenProvider;
import com.blog.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    private static final int TOKEN_TTL_HOURS = 2;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        if (loginDTO == null || isBlank(loginDTO.getUsername()) || isBlank(loginDTO.getPassword())) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }

        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, loginDTO.getUsername()));
        if (admin == null) {
            log.warn("Login failed: user not found: {}", loginDTO.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            log.warn("Login failed: wrong password for user: {}", loginDTO.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(admin.getId());
        redisTemplate.opsForValue().set(
                RedisKeyUtil.adminTokenKey(admin.getId()), token,
                Duration.ofHours(TOKEN_TTL_HOURS));

        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", adminVO);
        return result;
    }

    @Override
    public void logout(Long adminId) {
        if (adminId == null) return;
        redisTemplate.delete(RedisKeyUtil.adminTokenKey(adminId));
    }

    @Override
    public AdminVO getProfile(Long adminId) {
        if (adminId == null) {
            throw new BusinessException(400, "管理员ID不能为空");
        }
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        return vo;
    }

    @Override
    @Transactional
    public void updateProfile(Long adminId, AdminInfoDTO dto) {
        if (adminId == null) {
            throw new BusinessException(400, "管理员ID不能为空");
        }
        if (dto == null) {
            throw new BusinessException(400, "更新信息不能为空");
        }

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            admin.setNickname(dto.getNickname().trim());
        }
        if (dto.getAvatar() != null) {
            admin.setAvatar(dto.getAvatar().trim());
        }
        if (dto.getEmail() != null) {
            admin.setEmail(dto.getEmail().trim());
        }
        adminMapper.updateById(admin);
    }

    @Override
    @Transactional
    public void changePassword(Long adminId, PasswordChangeDTO dto) {
        if (adminId == null) {
            throw new BusinessException(400, "管理员ID不能为空");
        }
        if (dto == null || isBlank(dto.getOldPassword()) || isBlank(dto.getNewPassword())) {
            throw new BusinessException(400, "旧密码和新密码不能为空");
        }
        if (dto.getNewPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(400, "新密码长度不能少于" + MIN_PASSWORD_LENGTH + "位");
        }
        if (dto.getNewPassword().length() > MAX_PASSWORD_LENGTH) {
            throw new BusinessException(400, "新密码长度不能超过" + MAX_PASSWORD_LENGTH + "位");
        }

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), admin.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }
        admin.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        adminMapper.updateById(admin);

        // Invalidate all existing tokens
        redisTemplate.delete(RedisKeyUtil.adminTokenKey(adminId));
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
