package com.blog.util;

public class RedisKeyUtil {

    private static final String PREFIX = "blog:";

    // Cache keys
    public static String articleListCache(Integer categoryId, long page, long size) {
        return PREFIX + "cache:article:list:" + (categoryId != null ? categoryId : "all") + ":" + page + ":" + size;
    }

    public static String articleDetailCache(Long articleId) {
        return PREFIX + "cache:article:detail:" + articleId;
    }

    public static String articleHotCache() {
        return PREFIX + "cache:article:hot";
    }

    public static String articleArchiveCache() {
        return PREFIX + "cache:article:archive";
    }

    public static String categoryListCache() {
        return PREFIX + "cache:category:list";
    }

    public static String tagListCache() {
        return PREFIX + "cache:tag:list";
    }

    public static String siteConfigCache() {
        return PREFIX + "cache:site:config";
    }

    public static String articleViewCount(Long articleId) {
        return PREFIX + "cache:article:view:" + articleId;
    }

    // Token
    public static String adminTokenKey(Long adminId) {
        return PREFIX + "token:admin:" + adminId;
    }

    public static String userTokenKey(Long userId) {
        return PREFIX + "token:user:" + userId;
    }

    // Rate limiting
    public static String agentRateLimitKey(Long agentId, String ip) {
        return PREFIX + "rate:agent:" + agentId + ":" + ip;
    }

    public static String loginRateLimitKey(String ip) {
        return PREFIX + "rate:login:" + ip;
    }

    // Agent conversation
    public static String agentConversationKey(Long agentId, String sessionId) {
        return PREFIX + "agent:conversation:" + agentId + ":" + sessionId;
    }

    public static String agentSessionKey(String sessionId) {
        return PREFIX + "agent:session:" + sessionId + ":agentId";
    }

    public static String sensitiveHitKey(String ip) {
        return PREFIX + "agent:sensitive:hit:" + ip;
    }

    // Cache prefix patterns for eviction
    public static String articleListPrefix() {
        return PREFIX + "cache:article:list:";
    }

    public static String articleDetailPrefix() {
        return PREFIX + "cache:article:detail:";
    }
}
