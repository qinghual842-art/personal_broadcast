package com.blog.module.article.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private List<String> tags;
    private Integer status;
    private Integer isTop;
    private Integer viewCount;
    private Integer commentCount;
    private LocalDateTime createTime;
}
