package com.Wisdom_Nurture_Garden.demo.service.impl;

import com.Wisdom_Nurture_Garden.demo.dao.BindingMapper;
import com.Wisdom_Nurture_Garden.demo.dao.CheckinMapper;
import com.Wisdom_Nurture_Garden.demo.dao.UsersMapper;
import com.Wisdom_Nurture_Garden.demo.entity.Binding;
import com.Wisdom_Nurture_Garden.demo.entity.Checkin;
import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.CheckinService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private CheckinMapper checkinMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private BindingMapper bindingMapper;
    @Override
    public boolean submitCheckin(Checkin checkin) {
        Integer elderId = checkin.getElderId();
        if (elderId == null || elderId <= 0) {
            System.out.println("elderId为空，拒绝插入");
            return false;
        }

        // 检查老人账户是否存在
        Users elder = usersMapper.selectById(elderId);
        if (elder == null || elder.getRole() != 2) {
            System.out.println("老人账户不存在或角色错误");
            return false;
        }

        // 检查是否重复打卡（同一天内只允许一次）
        LocalDate today = LocalDate.now();
        QueryWrapper<Checkin> query = new QueryWrapper<>();
        query.eq("elder_id", elderId)
                .apply("DATE(create_time) = {0}", today);

        Long count = checkinMapper.selectCount(query);
        if (count != null && count > 0) {
            System.out.println("今天已打卡，禁止重复提交");
            return false;
        }

        // 插入打卡记录
        checkin.setCreateTime(LocalDateTime.now());
        int result = checkinMapper.insert(checkin);

        System.out.println(result > 0 ? "打卡成功" : "插入失败");
        return result > 0;
    }
    @Override
    public List<Checkin> getTodayCheckinByChild(Integer childId) {
        if (childId == null) {
            System.out.println("子女ID为空");
            return Collections.emptyList();
        }

        // Step 1: 查找绑定的老人
        QueryWrapper<Binding> bindingQuery = new QueryWrapper<>();
        bindingQuery.eq("child_id", childId);
        Binding binding = bindingMapper.selectOne(bindingQuery);

        if (binding == null) {
            System.out.println("该子女尚未绑定老人");
            return Collections.emptyList();
        }

        // Step 2: 查询老人的当天打卡
        Integer elderId = binding.getElderId();
        LocalDate today = LocalDate.now();

        QueryWrapper<Checkin> checkinQuery = new QueryWrapper<>();
        checkinQuery.eq("elder_id", elderId)
                .apply("DATE(create_time) = {0}", today);

        List<Checkin> list = checkinMapper.selectList(checkinQuery);
        System.out.println("返回 " + list.size() + " 条打卡记录");

        return list;
    }
}
