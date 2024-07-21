package com.example.mapper;

import com.example.common.entity.LoginLog;
import com.example.common.entity.User;
import com.example.common.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insertLoginLog(LoginLog loginLog);
    User selectUserByPhone(Long phone);
    int insertUser(User user);
    UserInfo selectUserInfoByPhone(Long phone);

    int insertUserInfo(UserInfo userInfo);

    int updateUserInfoByPhone(@Param("phone")Long phone,@Param("nickName") String nickName);
}
