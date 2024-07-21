package com.example.controller;

import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.common.entity.LoginLog;
import com.example.common.entity.User;
import com.example.common.entity.UserInfo;
import com.example.common.entity.UserResponse;
import com.example.service.UserService;
import com.example.utils.MD5Util;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private UserService userService;

    @Resource
    private MD5Util md5Util;

    @RequestMapping("/initData")
    @Transactional
    public Result<String> initData() throws Exception{
        List<User> users=initUsers(500);
        createToken(users);
        return ResultUtil.success("添加用户500个");
    }

    private void createToken(List<User> users) throws Exception{
        File file =new File("C:/website/tokens.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf=new RandomAccessFile(file,"rw");
        boolean createFileOk=file.createNewFile();
        System.out.println("创建文件成功");
        raf.seek(0);
        System.out.println(users.get(0).getPassword());
        for(int i=0;i<users.size();i++){
            User user=users.get(i);
            UserResponse userResponse=userService.insertLoginLog(user.getPhone(), "123456","0:0:0:0:0:0:0:1",null);
            String row=userResponse.getToken();
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
        }
        raf.close();

    }

    private List<User> initUsers(int count) throws Exception{
        List<User> users=new ArrayList<User>();
        List<UserInfo> userInfos=new ArrayList<UserInfo>();
        for(int i=0;i<count;i++){
            User user=new User();
            user.setPhone(13000000L+i);
            user.setPassword(md5Util.InputPassword_To_DBPassword("123456","abcd"));
            user.setSalt("abcd");
            users.add(user);
            UserInfo userInfo=new UserInfo();
            userInfo.setNickName("测试账号"+i);
            userInfo.setPhone(user.getPhone());
            userInfos.add(userInfo);
        }
        insertUsersToDB(users);
        insertUserInfosToDB(userInfos);
        return users;
    }

    private static Connection getConn() throws Exception{
        String url="jdbc:mysql://127.0.0.1:3306/data2?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String userName="root";
        String password="wangzihao8497";
        String driver="com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,userName,password);
    }

    private void insertUsersToDB(List<User> users) throws Exception{
        Connection conn=getConn();
        String sql="insert into usr(phone,password,salt) values(?,?,?)";
        PreparedStatement preparedStatement=conn.prepareStatement(sql);
        for(int i=0;i<users.size();i++){
            User user=users.get(i);
            preparedStatement.setLong(1,user.getPhone());
            preparedStatement.setString(2,user.getPassword());
            preparedStatement.setString(3,user.getSalt());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.close();
        conn.close();
    }

    private void insertUserInfosToDB(List<UserInfo> userInfos) throws Exception{
        Connection conn=getConn();
        String sql="insert into usr_info(phone,nickName) values(?,?)";
        PreparedStatement preparedStatement=conn.prepareStatement(sql);
        for(int i=0;i<userInfos.size();i++){
            UserInfo userInfo=userInfos.get(i);
            preparedStatement.setLong(1,userInfo.getPhone());
            preparedStatement.setString(2,userInfo.getNickName());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.close();
        conn.close();
    }

}
