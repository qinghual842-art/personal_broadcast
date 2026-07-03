package com.blog.module.admin.controller;

import com.blog.common.Result;
import com.blog.common.ResultCode;
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
@RequestMapping("/api/v1/admin")
public class FileController {

    @Value("${blog.upload.path:./uploads}")
    private String uploadPath;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 2 * 1024 * 1024;

    @PostMapping("/upload/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestHeader("Authorization") String token,
                                                     @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail(ResultCode.BAD_REQUEST, "文件不能为空");
        }

        if (file.getSize() > MAX_SIZE) {
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
            log.error("Avatar upload failed", e);
            return Result.fail(ResultCode.INTERNAL_ERROR, "头像上传失败");
        }
    }
}
