package com.Wisdom_Nurture_Garden.demo.service.impl;

import com.Wisdom_Nurture_Garden.demo.dao.CheckinMapper;
import com.Wisdom_Nurture_Garden.demo.entity.Checkin;
import com.Wisdom_Nurture_Garden.demo.service.CheckinService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private CheckinMapper checkinMapper;

    @Override
    public boolean submitCheckin(Checkin checkin) {
        return checkinMapper.insert(checkin) > 0;
    }

    @Override
    public List<Checkin> getTodayCheckinByElder(Integer elderId) {
        LocalDate today = LocalDate.now();
        QueryWrapper<Checkin> query = new QueryWrapper<>();
        query.eq("elder_id", elderId)
                .apply("DATE(create_time) = {0}", today);
        return checkinMapper.selectList(query);
    }
}
