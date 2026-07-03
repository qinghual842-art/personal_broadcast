package com.blog.module.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentSubmitDTO {
    @NotBlank(message = "昵称不能为空")
    private String authorName;
    private String authorEmail;
    @NotBlank(message = "评论内容不能为空")
    private String content;
    private Long parentId;
}
