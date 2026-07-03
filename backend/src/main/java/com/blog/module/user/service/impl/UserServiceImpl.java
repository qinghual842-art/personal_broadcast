package com.blog.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.exception.BusinessException;
import com.blog.module.user.dto.UserLoginDTO;
import com.blog.module.user.dto.UserRegisterDTO;
import com.blog.module.user.entity.User;
import com.blog.module.user.mapper.UserMapper;
import com.blog.module.user.service.UserService;
import com.blog.module.user.vo.UserVO;
import com.blog.security.JwtTokenProvider;
import com.blog.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    private static final int TOKEN_TTL_HOURS = 72;
    private static final int MIN_PASSWORD_LENGTH = 6;

    @Override
    @Transactional
    public Map<String, Object> register(UserRegisterDTO dto) {
        if (dto == null || isBlank(dto.getUsername())) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (isBlank(dto.getPassword()) || dto.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(400, "密码至少" + MIN_PASSWORD_LENGTH + "位");
        }
        if (userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()))) {
            throw new BusinessException(400, "用户名已被注册");
        }
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null && !dto.getNickname().isBlank() ? dto.getNickname().trim() : dto.getUsername());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) user.setEmail(dto.getEmail().trim());
        if (dto.getAvatar() != null && !dto.getAvatar().isBlank()) user.setAvatar(dto.getAvatar().trim());
        userMapper.insert(user);

        String token = jwtTokenProvider.generateUserToken(user.getId());
        redisTemplate.opsForValue().set(RedisKeyUtil.userTokenKey(user.getId()), token, Duration.ofHours(TOKEN_TTL_HOURS));

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", toVO(user));
        return result;
    }

    @Override
    public Map<String, Object> login(UserLoginDTO dto) {
        if (dto == null || isBlank(dto.getUsername()) || isBlank(dto.getPassword())) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        String token = jwtTokenProvider.generateUserToken(user.getId());
        redisTemplate.opsForValue().set(RedisKeyUtil.userTokenKey(user.getId()), token, Duration.ofHours(TOKEN_TTL_HOURS));
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", toVO(user));
        return result;
    }

    @Override
    public void logout(Long userId) {
        if (userId != null) redisTemplate.delete(RedisKeyUtil.userTokenKey(userId));
    }

    @Override
    public UserVO getProfile(Long userId) {
        if (userId == null) throw new BusinessException(400, "用户ID不能为空");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        return toVO(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String nickname, String avatar, String email, String bio) {
        if (userId == null) throw new BusinessException(400, "用户ID不能为空");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (nickname != null && !nickname.isBlank()) user.setNickname(nickname.trim());
        if (avatar != null) user.setAvatar(avatar.trim());
        if (email != null) user.setEmail(email.trim());
        if (bio != null) user.setBio(bio.trim());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null) throw new BusinessException(400, "用户ID不能为空");
        if (isBlank(oldPassword) || isBlank(newPassword)) throw new BusinessException(400, "密码不能为空");
        if (newPassword.length() < MIN_PASSWORD_LENGTH) throw new BusinessException(400, "新密码至少" + MIN_PASSWORD_LENGTH + "位");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) throw new BusinessException(400, "旧密码错误");
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        redisTemplate.delete(RedisKeyUtil.userTokenKey(userId));
    }

    @Override
    public UserVO getUserById(Long id) {
        if (id == null) return null;
        User user = userMapper.selectById(id);
        return user != null ? toVO(user) : null;
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    private boolean isBlank(String str) { return str == null || str.isBlank(); }
}
