package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long phone;//手机号码
    private String password;//密码
    private String salt;//加密使用的盐

    public User(Long phone,String password){
        this.phone=phone;
        this.password=password;
    }
}
