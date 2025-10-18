package com.Wisdom_Nurture_Garden.demo.service.impl;

import com.Wisdom_Nurture_Garden.demo.dao.UsersMapper;
import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.UsersService;
import com.Wisdom_Nurture_Garden.demo.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public String login(String username, String password, Integer role) {
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("name", username).eq("role", role);
        Users user = usersMapper.selectOne(query);

        if (user == null) {
            return null;
        }

        // 验证密码（MD5加密匹配）
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            return null;
        }

        // 登录成功生成 token
        return JwtUtil.generateToken(user.getId(), user.getName(), user.getRole());

    }

    @Override
    public boolean register(Users user) {
        // 检查用户名是否重复
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("name", user.getName());
        if (usersMapper.selectOne(query) != null) {
            return false;
        }

        // 密码加密
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Password);

        // 设置时间
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 插入数据库
        return usersMapper.insert(user) > 0;
    }
}
