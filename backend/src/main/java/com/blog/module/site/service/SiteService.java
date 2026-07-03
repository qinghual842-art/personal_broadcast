package com.blog.module.site.service;

import java.util.Map;

public interface SiteService {
    Map<String, String> getPublicConfig();
    Map<String, String> getAllConfig();
    void updateConfig(Map<String, String> configs);
}
