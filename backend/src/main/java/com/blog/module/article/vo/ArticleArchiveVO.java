package com.blog.module.article.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArticleArchiveVO {
    private String yearMonth;
    private List<ArticleVO> articles;
}
