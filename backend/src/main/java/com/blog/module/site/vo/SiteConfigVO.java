package com.blog.module.site.vo;

import lombok.Data;

import java.util.Map;

@Data
public class SiteConfigVO {
    private Map<String, String> configs;
}
