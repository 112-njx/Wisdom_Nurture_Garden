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
    public Users wechatLogin(String appid, String secret, String code, String nickname, String avatarUrl) {
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + appid
                + "&secret=" + secret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        Map<String, Object> wxRes = HttpClientUtil.getForMap(url);
        String openid = (String) wxRes.get("openid");
        if (openid == null) return null;

        // 查询是否已有用户
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("openid", openid);
        Users user = usersMapper.selectOne(query);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (user == null) {
            user = new Users();
            user.setOpenid(openid);
            user.setName(nickname != null ? nickname : "wx_user_" + openid.substring(openid.length() - 6));
            user.setImg(avatarUrl != null ? avatarUrl : "");
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes())); // 默认密码
            user.setRole(0); // 未分类
            user.setCreateTime(now);
            user.setUpdateTime(now);
            usersMapper.insert(user);
        } else {
            // 更新头像和昵称（微信资料更新）
            user.setName(nickname);
            user.setImg(avatarUrl);
            user.setUpdateTime(now);
            usersMapper.updateById(user);
        }
        return user;
    }

    @Override
    public boolean updateUserInfo(Users user) {
        Users dbUser = usersMapper.selectById(user.getId());
        if (dbUser == null) return false;

        dbUser.setName(user.getName());
        dbUser.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        dbUser.setRole(user.getRole());
        dbUser.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return usersMapper.updateById(dbUser) > 0;
    }
}
