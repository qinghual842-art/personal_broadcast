package com.blog.module.user.controller;

import com.blog.common.Result;
import com.blog.module.user.dto.UserLoginDTO;
import com.blog.module.user.dto.UserRegisterDTO;
import com.blog.module.user.service.UserService;
import com.blog.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserRegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        Long userId = UserContext.getUserId();
        userService.logout(userId);
        return Result.success();
    }

    @GetMapping("/profile")
    public Result<?> getProfile() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Map<String, String> body) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        userService.updateProfile(userId,
                body.get("nickname"), body.get("avatar"),
                body.get("email"), body.get("bio"));
        return Result.success();
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.fail(401, "请先登录");
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.success();
    }
}
