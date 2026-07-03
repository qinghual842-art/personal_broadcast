package com.blog.module.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagSaveDTO {
    @NotBlank(message = "标签名称不能为空")
    private String name;
}
