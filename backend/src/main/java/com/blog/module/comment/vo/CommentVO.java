package com.blog.module.comment.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String authorName;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
}
