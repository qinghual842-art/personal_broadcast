package com.blog.module.tag.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TagVO {
    private Long id;
    private String name;
    private Integer articleCount;
    private LocalDateTime createTime;
}
