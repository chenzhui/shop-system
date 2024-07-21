package com.example.service;


import com.example.common.entity.UserInfo;
import com.example.common.entity.UserResponse;

public interface UserService {
    UserResponse insertLoginLog(Long phone, String password, String ip, String token);
    int insertUser(Long phone,String password,String nickName,String ip);
    UserInfo selectUserInfoByPhone(Long phone);

    int updateUserInfoByPhone(Long phone,String nickName);

}
