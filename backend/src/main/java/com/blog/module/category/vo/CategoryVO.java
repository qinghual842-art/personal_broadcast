package com.blog.module.category.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Integer articleCount;
    private LocalDateTime createTime;
}
