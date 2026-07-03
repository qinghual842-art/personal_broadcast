package com.blog.module.article.dto;

import com.blog.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleQueryDTO extends PageQuery {
    private Long categoryId;
    private Long tagId;
    private String keyword;
    private Integer status;
    private Long userId;
}
