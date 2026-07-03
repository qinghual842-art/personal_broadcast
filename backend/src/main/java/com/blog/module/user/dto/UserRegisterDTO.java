package com.blog.module.user.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
}
