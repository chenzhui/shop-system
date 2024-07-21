package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable{
    private Long  phone;
    private String nickName;
    private String head;
    private String birthDay;
    private String info;
    private String registerIp;

    public UserInfo(Long phone,String nickName,String registerIp){
        this.phone=phone;
        this.nickName=nickName;
        this.registerIp=registerIp;
    }
}
