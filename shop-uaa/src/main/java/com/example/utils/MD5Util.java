package com.example.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    public String Md5(String str){
        return DigestUtils.md5Hex(str);
    }

    public String InputPassword_To_DBPassword(String inputPassword,String salt){
        String str=salt.charAt(2)+inputPassword+salt.charAt(0);
        return Md5(str);
    }
}
