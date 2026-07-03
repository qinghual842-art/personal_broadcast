package com.blog.task;

import com.blog.module.article.entity.Article;
import com.blog.module.article.mapper.ArticleMapper;
import com.blog.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEvictTask {

    private final StringRedisTemplate redisTemplate;
    private final ArticleMapper articleMapper;

    @Scheduled(fixedRate = 300000)
    public void syncViewCounts() {
        Set<String> keys = redisTemplate.keys("blog:cache:article:view:*");
        if (keys != null) {
            for (String key : keys) {
                try {
                    String idStr = key.substring(key.lastIndexOf(":") + 1);
                    Long articleId = Long.parseLong(idStr);
                    String countStr = redisTemplate.opsForValue().get(key);
                    if (countStr != null) {
                        int count = Integer.parseInt(countStr);
                        Article article = articleMapper.selectById(articleId);
                        if (article != null) {
                            article.setViewCount(article.getViewCount() + count);
                            articleMapper.updateById(article);
                        }
                        redisTemplate.delete(key);
                    }
                } catch (Exception e) {
                    log.warn("Failed to sync view count for key: {}", key, e);
                }
            }
        }
    }
}
