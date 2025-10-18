package com.Wisdom_Nurture_Garden.demo.controller;

import com.Wisdom_Nurture_Garden.demo.entity.Checkin;
import com.Wisdom_Nurture_Garden.demo.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    //老人端提交健康数据
    @PostMapping
    public String submit(@RequestBody Checkin checkin) {
        boolean success = checkinService.submitCheckin(checkin);
        return success ? "打卡成功" : "打卡失败";
    }

    //子女端查看绑定老人当天打卡数据
    @GetMapping("/today/{elderId}")
    public List<Checkin> getToday(@PathVariable int elderId) {
        return checkinService.getTodayCheckinByElder(elderId);
    }
}
