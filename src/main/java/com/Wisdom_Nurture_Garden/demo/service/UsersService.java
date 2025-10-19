package com.Wisdom_Nurture_Garden.demo.service;

import com.Wisdom_Nurture_Garden.demo.entity.Users;

public interface UsersService {
    // 登录返回 JWT token
    String login(String username, String password, Integer role);

    // 注册功能
    boolean register(Users user);

    //微信登录
    Users wechatLogin(String appid, String secret, String code);
}
