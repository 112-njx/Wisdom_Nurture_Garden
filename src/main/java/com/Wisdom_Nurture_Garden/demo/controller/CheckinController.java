package com.Wisdom_Nurture_Garden.demo.controller;

import com.Wisdom_Nurture_Garden.demo.entity.Checkin;
import com.Wisdom_Nurture_Garden.demo.service.CheckinService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin
public class CheckinController {

    @Autowired
    private CheckinService checkinService;
    @PostMapping
    public String submit(@RequestBody Checkin checkin, HttpServletRequest request) {
        Integer elderId = (Integer) request.getAttribute("userId");
        checkin.setElderId(elderId); // 覆盖前端传来的 id（防止恶意修改）

        boolean success = checkinService.submitCheckin(checkin);
        return success ? "打卡成功" : "打卡失败或今天已打过卡";
    }
    @GetMapping("/today")
    public List<Checkin> getToday(HttpServletRequest request) {
        Integer childId = (Integer) request.getAttribute("userId");
        return checkinService.getTodayCheckinByChild(childId);
    }
}
