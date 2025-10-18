package com.Wisdom_Nurture_Garden.demo.controller;

import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.UsersService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.Wisdom_Nurture_Garden.demo.dao.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersMapper usersMapper;

    // 注册接口
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Users user) {
        Map<String, Object> result = new HashMap<>();

        boolean success = usersService.register(user);
        if (success) {
            result.put("code", 200);
            result.put("message", "注册成功");
        } else {
            result.put("code", 400);
            result.put("message", "用户名已存在或注册失败");
        }
        return result;
    }

    // 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> loginData) {
        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        Integer role = (Integer) loginData.get("role");

        String token = usersService.login(username, password, role);
        Map<String, Object> result = new HashMap<>();

        if (token == null) {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return result;
        }

        // 登录成功
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("name", username).eq("role", role);
        Users user = usersMapper.selectOne(query);

        result.put("code", 200);
        result.put("message", "登录成功！");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        result.put("data", data);

        return result;
    }

}
