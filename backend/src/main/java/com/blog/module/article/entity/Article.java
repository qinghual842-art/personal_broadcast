package com.blog.module.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 作者用户ID，0=管理员发布 */
    private Long userId;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String contentHtml;
    private Long categoryId;
    private String coverImage;
    /** 0=draft, 1=published */
    private Integer status;
    private Integer isTop;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
