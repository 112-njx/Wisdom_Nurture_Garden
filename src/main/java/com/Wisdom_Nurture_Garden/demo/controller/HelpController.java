package com.Wisdom_Nurture_Garden.demo.controller;

import com.Wisdom_Nurture_Garden.demo.entity.HelpRequest;
import com.Wisdom_Nurture_Garden.demo.service.HelpService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/help")
@CrossOrigin
public class HelpController {

    @Autowired
    private HelpService helpService;

    // 老人发起求助
    @PostMapping
    public Map<String, Object> help(@RequestBody(required = false) Map<String, Object> body,
                                    HttpServletRequest request) {
        Integer elderId = (Integer) request.getAttribute("userId"); // 从 token 获取
        String message = (body != null && body.get("message") != null)
                ? body.get("message").toString()
                : "老人发出了求助请求！";

        boolean success = helpService.sendHelpRequest(elderId);

        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "求助已发送");
        } else {
            result.put("code", 400);
            result.put("message", "求助失败，未找到绑定的子女");
        }
        return result;
    }
}
