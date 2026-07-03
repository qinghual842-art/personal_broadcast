package com.blog.module.article.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDetailVO extends ArticleVO {
    private String content;
    private String contentHtml;
    private ArticleNavVO prev;
    private ArticleNavVO next;
    private Boolean liked = false;
    private Long userId;
    private String authorName;
    private String authorAvatar;

    @Data
    public static class ArticleNavVO {
        private Long id;
        private String title;
        private String slug;
    }
}
