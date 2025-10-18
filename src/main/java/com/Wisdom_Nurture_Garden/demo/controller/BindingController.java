package com.Wisdom_Nurture_Garden.demo.controller;

import com.Wisdom_Nurture_Garden.demo.entity.Binding;
import com.Wisdom_Nurture_Garden.demo.service.BindingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bind")
@CrossOrigin
public class BindingController {

    @Autowired
    private BindingService bindingService;

    // 子女绑定老人
    @PostMapping
    public String bind(@RequestBody BindRequest request, HttpServletRequest httpRequest) {
        Integer childId = (Integer) httpRequest.getAttribute("userId"); // ✅ 从 token 获取
        boolean success = bindingService.bindElder(childId, request.getElderName(), request.getElderPassword());
        return success ? "绑定成功" : "绑定失败或已绑定";
    }

    // 子女端查询绑定的老人信息
    @GetMapping("/child")
    public Binding getElder(HttpServletRequest request) {
        Integer childId = (Integer) request.getAttribute("userId");
        return bindingService.getBindingByChild(childId);
    }

    // 老人端查询绑定的子女信息
    @GetMapping("/elder")
    public Binding getChild(HttpServletRequest request) {
        Integer elderId = (Integer) request.getAttribute("userId");
        return bindingService.getBindingByElder(elderId);
    }

    @Data
    static class BindRequest {
        private String elderName;
        private String elderPassword;
    }
}
