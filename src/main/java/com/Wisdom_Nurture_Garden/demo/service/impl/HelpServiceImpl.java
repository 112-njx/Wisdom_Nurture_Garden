package com.Wisdom_Nurture_Garden.demo.service.impl;

import com.Wisdom_Nurture_Garden.demo.dao.BindingMapper;
import com.Wisdom_Nurture_Garden.demo.dao.HelpMapper;
import com.Wisdom_Nurture_Garden.demo.dao.UsersMapper;
import com.Wisdom_Nurture_Garden.demo.entity.Binding;
import com.Wisdom_Nurture_Garden.demo.entity.HelpRequest;
import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.HelpService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HelpServiceImpl implements HelpService {

    @Autowired
    private HelpMapper helpMapper;

    @Autowired
    private BindingMapper bindingMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean sendHelpRequest(Integer elderId) {
        // 找绑定关系
        QueryWrapper<Binding> query = new QueryWrapper<>();
        query.eq("elder_id", elderId);
        Binding binding = bindingMapper.selectOne(query);
        if (binding == null) {
            return false; // 没绑定
        }

        Integer childId = binding.getChildId();
        Users child = usersMapper.selectById(childId);
        if (child == null) return false;

        // 保存求助记录
        HelpRequest request = new HelpRequest();
        request.setElderId(elderId);
        request.setChildId(childId);
        request.setCreateTime(LocalDateTime.now());
        helpMapper.insert(request);

        // 模拟通知（你之后可以替换为真正的推送）
        System.out.println("求助提醒发送成功");

        return true;
    }
}
