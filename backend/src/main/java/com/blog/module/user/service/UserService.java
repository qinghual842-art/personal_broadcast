package com.blog.module.user.service;

import com.blog.module.user.dto.UserLoginDTO;
import com.blog.module.user.dto.UserRegisterDTO;
import com.blog.module.user.vo.UserVO;

import java.util.Map;

public interface UserService {
    UserVO register(UserRegisterDTO dto);
    Map<String, Object> login(UserLoginDTO dto);
    void logout(Long userId);
    UserVO getProfile(Long userId);
    void updateProfile(Long userId, String nickname, String avatar, String email, String bio);
    void changePassword(Long userId, String oldPassword, String newPassword);
    UserVO getUserById(Long id);
}
