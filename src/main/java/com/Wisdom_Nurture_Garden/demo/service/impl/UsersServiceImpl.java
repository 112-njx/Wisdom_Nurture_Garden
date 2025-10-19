package com.Wisdom_Nurture_Garden.demo.service.impl;

import com.Wisdom_Nurture_Garden.demo.dao.UsersMapper;
import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.UsersService;
import com.Wisdom_Nurture_Garden.demo.utils.HttpClientUtil;
import com.Wisdom_Nurture_Garden.demo.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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

    @Override
    public Users wechatLogin(String appid, String secret, String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + appid
                + "&secret=" + secret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        Map<String, Object> wxRes = HttpClientUtil.getForMap(url);  // 要实现 HTTP 请求并把 JSON 转为 Map
        String openid = (String) wxRes.get("openid");
        String sessionKey = (String) wxRes.get("session_key");
        if (openid == null) {
            // 失败
            return null;
        }

        // 查找用户是否存在
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("openid", openid);
        Users user = usersMapper.selectOne(query);

        // 不存在，则创建新用户
        if (user == null) {
            user = new Users();
            user.setName("wx_" + openid.substring(openid.length()-6));  // 你可按规则生成默认用户名
            user.setPassword("");  // 微信登录暂时无密码
            user.setImg("");       // 可设置默认头像
            user.setRole(1);       // 或你自己决定是子女还是老人，可能需让用户选择
            user.setOpenid(openid);
            // 设置 create_time / update_time 等
            usersMapper.insert(user);
        }
        return user;
    }
}
