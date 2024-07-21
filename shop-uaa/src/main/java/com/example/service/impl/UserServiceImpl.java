package com.example.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.example.common.entity.UserInfo;
import com.example.common.entity.LoginLog;
import com.example.common.entity.User;
import com.example.common.entity.UserResponse;
import com.example.common.exception.BusinessException;
import com.example.mapper.UserMapper;
import com.example.mq.MQConstant;
import com.example.msg.UAACodeMsg;
import com.example.common.redis.CommonRedisKey;
import com.example.redis.UaaRedisKey;
import com.example.service.UserService;
import com.example.utils.MD5Util;
import com.example.utils.RandomFourUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@CacheConfig(cacheNames = "user_info")
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper mapper;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RandomFourUtil randomFourUtil;
    @Resource
    private MD5Util md5Util;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public int insertUser(Long phone, String password, String nickName, String ip) {
        String salt=randomFourUtil.GetFour();
        User user=new User(phone,md5Util.InputPassword_To_DBPassword(password,salt),salt);
        mapper.insertUser(user);
        UserInfo userInfo=new UserInfo(phone,nickName,ip);
        mapper.insertUserInfo(userInfo);
        return 1;
    }


    private User getUser(Long phone) {
        User user;
        String hashKey = UaaRedisKey.USER_HASH.getRealKey("");
        String zSetKey = UaaRedisKey.USER_ZSET.getRealKey("");
        String userPhone = String.valueOf(phone);
        String objStr = (String) stringRedisTemplate.opsForHash().get(hashKey, String.valueOf(phone));
        if (StringUtils.isEmpty(objStr)) {
            user = mapper.selectUserByPhone(phone);
            stringRedisTemplate.opsForHash().put(hashKey, userPhone, JSON.toJSONString(user));
        } else {
            user = JSON.parseObject(objStr, User.class);
        }
        stringRedisTemplate.opsForZSet().add(zSetKey, userPhone, System.currentTimeMillis());
        return user;
    }

    @Override
    public UserResponse insertLoginLog(Long phone, String password, String ip, String token) {
        LoginLog loginLog = new LoginLog(phone, ip, new Date());
        rocketMQTemplate.sendOneWay("LOGIN_TOPIC",loginLog);   //往消息队列里塞日志
        UserInfo userInfo = getByToken(token);   //判断用户是否已经登录
        if (userInfo == null){
            User user=this.getUser(phone);
            if (user == null || !user.getPassword().equals(md5Util.InputPassword_To_DBPassword(password, user.getSalt()))) {
                loginLog.setState(LoginLog.LOGIN_FAIL);
                rocketMQTemplate.sendOneWay(MQConstant.LOGIN_TOPIC + ":" + LoginLog.LOGIN_FAIL, loginLog);//往MQ中发送:登录失败
                throw new BusinessException(UAACodeMsg.LOGIN_ERROR);
            }
            userInfo = mapper.selectUserInfoByPhone(phone);
            userInfo.setRegisterIp(ip);
            token = createToken(userInfo);
            rocketMQTemplate.sendOneWay("LOGIN_TOPIC", loginLog);  //往MQ中发送:登录成功
        }
        return new UserResponse(token, userInfo);
    }

    @Override
    @Cacheable(key="#phone")
    public UserInfo selectUserInfoByPhone(Long phone) {
        return mapper.selectUserInfoByPhone(phone);
    }

    @Override
    @CacheEvict(key = "#phone")
    public int updateUserInfoByPhone(Long phone,String nickName) {
        return mapper.updateUserInfoByPhone(phone,nickName);
    }

    private String createToken(UserInfo userInfo) {
        String token = UUID.randomUUID().toString().replace("-", "");
        CommonRedisKey redisKey = CommonRedisKey.USER_TOKEN;
        stringRedisTemplate.opsForValue().set(redisKey.getRealKey(token), JSON.toJSONString(userInfo), redisKey.getExpireTime(), redisKey.getUnit());
        return token;
    }

    private UserInfo getByToken(String token) {
        String strObj = stringRedisTemplate.opsForValue().get(CommonRedisKey.USER_TOKEN.getRealKey(token));
        if (StringUtils.isEmpty(strObj)) {
            return null;
        }
        return JSON.parseObject(strObj, UserInfo.class);
    }

}
