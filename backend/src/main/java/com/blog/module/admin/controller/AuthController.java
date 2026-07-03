package com.blog.module.admin.controller;

import com.blog.common.Result;
import com.blog.module.admin.dto.AdminInfoDTO;
import com.blog.module.admin.dto.LoginDTO;
import com.blog.module.admin.dto.PasswordChangeDTO;
import com.blog.module.admin.service.AuthService;
import com.blog.module.admin.vo.AdminVO;
import com.blog.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/auth/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @PostMapping("/auth/logout")
    public Result<?> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            Long adminId = jwtTokenProvider.getAdminIdFromToken(token.substring(7));
            authService.logout(adminId);
        }
        return Result.success();
    }

    @GetMapping("/profile")
    public Result<AdminVO> profile(@RequestHeader("Authorization") String token) {
        Long adminId = jwtTokenProvider.getAdminIdFromToken(token.substring(7));
        return Result.success(authService.getProfile(adminId));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestHeader("Authorization") String token,
                                   @RequestBody AdminInfoDTO dto) {
        Long adminId = jwtTokenProvider.getAdminIdFromToken(token.substring(7));
        authService.updateProfile(adminId, dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestHeader("Authorization") String token,
                                    @Valid @RequestBody PasswordChangeDTO dto) {
        Long adminId = jwtTokenProvider.getAdminIdFromToken(token.substring(7));
        authService.changePassword(adminId, dto);
        return Result.success();
    }
}
