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

    @Value("${wechat.secret}")
    private String secret;

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    public Map<String, Object> wechatLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        Map<String, Object> result = new HashMap<>();

        if (code == null || code.isEmpty()) {
            result.put("code", 400);
            result.put("message", "code 不能为空");
            return result;
        }

        // 调用 service 做微信换取 openid + 用户处理
        Users user = usersService.wechatLogin(appid, secret, code);
        if (user == null) {
            result.put("code", 500);
            result.put("message", "微信登录失败");
            return result;
        }

        // 生成 token
        String token = JwtUtil.generateToken(user.getId(), user.getName(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);  // 注意：不要暴露敏感字段如 password

        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("data", data);
        return result;
    }
}
