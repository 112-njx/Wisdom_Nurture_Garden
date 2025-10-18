package com.Wisdom_Nurture_Garden.demo.controller;

import com.Wisdom_Nurture_Garden.demo.entity.Binding;
import com.Wisdom_Nurture_Garden.demo.service.BindingService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bind")
@CrossOrigin
public class BindingController {

    @Autowired
    private BindingService bindingService;

    //子女绑定老人
    @PostMapping
    public String bind(@RequestBody BindRequest request) {
        boolean success = bindingService.bindElder(request.getChildId(),request.getElderName(), request.getElderPassword());
        return success ? "绑定成功" : "绑定失败或已绑定";
    }

    @GetMapping("/child/{childId}")
    public Binding getElder(@PathVariable int childId) {
        return bindingService.getBindingByChild(childId);
    }

    @GetMapping("/elder/{elderId}")
    public Binding getChild(@PathVariable int elderId) {
        return bindingService.getBindingByElder(elderId);
    }

    @Data
    static class BindRequest {
        private int childId;
        private String elderName;
        private String elderPassword;
    }
}
