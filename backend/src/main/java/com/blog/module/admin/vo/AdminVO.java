package com.blog.module.admin.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private LocalDateTime createTime;
}
