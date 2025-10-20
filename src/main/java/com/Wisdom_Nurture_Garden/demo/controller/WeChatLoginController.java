package com.Wisdom_Nurture_Garden.demo.controller;

import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.UsersService;
import com.Wisdom_Nurture_Garden.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wechat")
@CrossOrigin
public class WeChatLoginController {

    @Value("wx93e5811a37775074")
    private String appid;

    @Value("d99885b18d66206a2b845f02731e445a")
    private String secret;

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    public Map<String, Object> wechatLogin(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("code");
        String nickname = (String) body.get("nickname");
        String avatarUrl = (String) body.get("avatarUrl");

        Map<String, Object> result = new HashMap<>();

        if (code == null || code.isEmpty()) {
            result.put("code", 400);
            result.put("message", "code不能为空");
            return result;
        }

        Users user = usersService.wechatLogin(appid, secret, code, nickname, avatarUrl);
        if (user == null) {
            result.put("code", 500);
            result.put("message", "微信登录失败");
            return result;
        }

        String token = JwtUtil.generateToken(user.getId(), user.getName(), user.getRole());

        result.put("code", 200);
        result.put("message", "登录成功");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        result.put("data", data);

        return result;
    }

    // 微信用户完善信息接口（绑定姓名、密码、角色）
    @PostMapping("/update")
    public Map<String, Object> updateUser(@RequestBody Users user) {
        Map<String, Object> result = new HashMap<>();

        boolean success = usersService.updateUserInfo(user);
        if (success) {
            result.put("code", 200);
            result.put("message", "信息完善成功");
        } else {
            result.put("code", 400);
            result.put("message", "更新失败");
        }
        return result;
    }

}
