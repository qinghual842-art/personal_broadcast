package com.blog.module.site.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.exception.BusinessException;
import com.blog.module.admin.entity.Admin;
import com.blog.module.admin.mapper.AdminMapper;
import com.blog.module.admin.vo.AdminVO;
import com.blog.module.site.entity.SiteConfig;
import com.blog.module.site.mapper.SiteConfigMapper;
import com.blog.module.site.service.SiteService;
import com.blog.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteConfigMapper siteConfigMapper;
    private final AdminMapper adminMapper;
    private final StringRedisTemplate redisTemplate;

    private static final List<String> PUBLIC_KEYS = List.of(
            "site_name", "site_desc", "copyright", "about_me",
            "social_links", "keywords", "site_logo", "site_favicon");

    @Override
    public Map<String, String> getPublicConfig() {
        List<SiteConfig> configs = siteConfigMapper.selectList(
                new LambdaQueryWrapper<SiteConfig>().in(SiteConfig::getConfigKey, PUBLIC_KEYS));
        return configs.stream().collect(Collectors.toMap(
                SiteConfig::getConfigKey,
                c -> Objects.toString(c.getConfigValue(), "")));
    }

    @Override
    public Map<String, String> getAllConfig() {
        List<SiteConfig> configs = siteConfigMapper.selectList(null);
        return configs.stream().collect(Collectors.toMap(
                SiteConfig::getConfigKey,
                c -> Objects.toString(c.getConfigValue(), "")));
    }

    @Override
    @Transactional
    public void updateConfig(Map<String, String> configs) {
        if (configs == null || configs.isEmpty()) return;

        // Batch load all existing configs to avoid N+1
        List<SiteConfig> existingConfigs = siteConfigMapper.selectList(null);
        Map<String, SiteConfig> existingMap = existingConfigs.stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, c -> c));

        List<SiteConfig> toUpdate = new ArrayList<>();
        List<SiteConfig> toInsert = new ArrayList<>();

        for (Map.Entry<String, String> entry : configs.entrySet()) {
            SiteConfig existing = existingMap.get(entry.getKey());
            if (existing != null) {
                existing.setConfigValue(entry.getValue());
                toUpdate.add(existing);
            } else {
                SiteConfig config = new SiteConfig();
                config.setConfigKey(entry.getKey());
                config.setConfigValue(entry.getValue());
                toInsert.add(config);
            }
        }

        // Batch operations
        if (!toUpdate.isEmpty()) {
            for (SiteConfig config : toUpdate) {
                siteConfigMapper.updateById(config);
            }
        }
        if (!toInsert.isEmpty()) {
            for (SiteConfig config : toInsert) {
                siteConfigMapper.insert(config);
            }
        }

        // Evict cache
        redisTemplate.delete(RedisKeyUtil.siteConfigCache());
    }

    @Override
    public AdminVO getOwnerInfo() {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin"));
        if (admin == null) return null;
        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        return vo;
    }
}
