package com.blog.module.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveDTO {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private String description;
    private Integer sortOrder;
}
