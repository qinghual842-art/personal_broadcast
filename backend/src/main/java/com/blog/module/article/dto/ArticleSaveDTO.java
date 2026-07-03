package com.blog.module.article.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ArticleSaveDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String summary;
    @NotBlank(message = "内容不能为空")
    private String content;
    private Long categoryId;
    private String coverImage;
    private Integer status;
    private Integer isTop;
    private List<Long> tagIds;
}
