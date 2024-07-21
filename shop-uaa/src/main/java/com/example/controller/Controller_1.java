package com.example.controller;

import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.service.UserService;
import com.example.utils.MD5Util;
import com.example.utils.RandomFourUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController()
@RequestMapping("/rl")
public class Controller_1 {
    @Resource
    private UserService userService;
    @Resource
    private RandomFourUtil randomFourUtil;

    @Autowired
    private MD5Util md5Util;

    @GetMapping("/register")
    public Result register(@RequestParam("phone") Long phone, @RequestParam("password") String password
                           , @RequestParam("nickName") String nickName
                           , @RequestHeader(value = "X-REAL-IP", required = false) String ip){
        return ResultUtil.success(this.userService.insertUser(phone,password,nickName,ip));
    }

    @GetMapping("/login")
    public Result login(@RequestParam("phone") Long phone,@RequestParam("password") String password
            ,@RequestHeader(value = "X-REAL-IP", required = false) String ip
            ,@RequestHeader(value = "token", required = false) String token){
        return ResultUtil.success(userService.insertLoginLog(phone,password, ip, token ));
    }

    @GetMapping("/getUserInfoByPhone")
    public Result getUserInfoByPhone(@RequestParam("phone") Long phone){
        return ResultUtil.success(userService.selectUserInfoByPhone(phone));
    }

    @GetMapping("/changeUserInfoByPhone")
    public Result changeUserInfoByPhone(@RequestParam("phone") Long phone,@RequestParam("nickName")String nickName){
        return ResultUtil.success(userService.updateUserInfoByPhone(phone,nickName));
    }


//    @GetMapping("updatePassword")
//    public Result updatePassword(@PathVariable("phone")Long phone,@PathVariable("oldPass")String oldPass,@PathVariable("newPass")String newPass){
//        return ResultUtil.success(userService.)
//    }

}
