package com.blog.module.site.controller;

import com.blog.common.Result;
import com.blog.module.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/site/config")
    public Result<Map<String, String>> getPublicConfig() {
        return Result.success(siteService.getPublicConfig());
    }

    @GetMapping("/admin/site/config")
    public Result<Map<String, String>> getAllConfig() {
        return Result.success(siteService.getAllConfig());
    }

    @PutMapping("/admin/site/config")
    public Result<?> updateConfig(@RequestBody Map<String, String> configs) {
        siteService.updateConfig(configs);
        return Result.success();
    }
}
