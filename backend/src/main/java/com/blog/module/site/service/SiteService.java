package com.blog.module.site.service;

import com.blog.module.admin.vo.AdminVO;

import java.util.Map;

public interface SiteService {
    Map<String, String> getPublicConfig();
    Map<String, String> getAllConfig();
    void updateConfig(Map<String, String> configs);
    AdminVO getOwnerInfo();
}
