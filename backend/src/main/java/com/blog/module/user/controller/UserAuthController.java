package com.blog.module.user.controller;

import com.blog.common.Result;
import com.blog.common.ResultCode;
import com.blog.module.user.dto.UserLoginDTO;
import com.blog.module.user.dto.UserRegisterDTO;
import com.blog.module.user.service.UserService;
import com.blog.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;

    @Value("${blog.upload.path:./uploads}")
    private String uploadPath;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserRegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    @PostMapping("/upload/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail(ResultCode.BAD_REQUEST, "文件不能为空");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            return Result.fail(ResultCode.BAD_REQUEST, "头像大小不能超过2MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return Result.fail(ResultCode.BAD_REQUEST, "仅支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        String filename = UUID.randomUUID().toString() + "." + extension;
        try {
            Path avatarDir = Paths.get(uploadPath, "avatars");
            Files.createDirectories(avatarDir);
            Files.copy(file.getInputStream(), avatarDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            Map<String, String> result = new HashMap<>();
            result.put("url", "/uploads/avatars/" + filename);
            return Result.success(result);
        } catch (Exception e) {
            log.error("User avatar upload failed", e);
            return Result.fail(ResultCode.INTERNAL_ERROR, "头像上传失败");
        }
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
