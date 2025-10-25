package com.Wisdom_Nurture_Garden.demo.service.impl;

import com.Wisdom_Nurture_Garden.demo.dao.BindingMapper;
import com.Wisdom_Nurture_Garden.demo.dao.UsersMapper;
import com.Wisdom_Nurture_Garden.demo.entity.Binding;
import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.HelpService;
import com.Wisdom_Nurture_Garden.demo.websocket.HelpWebSocketServer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelpServiceImpl implements HelpService {

    @Autowired
    private BindingMapper bindingMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean sendHelpRequest(Integer elderId) {
        QueryWrapper<Binding> query = new QueryWrapper<>();
        query.eq("elder_id", elderId);
        Binding binding = bindingMapper.selectOne(query);
        if (binding == null) {
            return false;
        }

        Integer childId = binding.getChildId();
        Users child = usersMapper.selectById(childId);
        if (child == null) return false;

        // 推送求助消息（WebSocket）
        String notify = "您的老人（ID：" + elderId + "）发出了求助请求！";
        HelpWebSocketServer.sendToChild(childId, notify);

        System.out.println("求助推送完成 -> 子女ID：" + childId + " 内容：" + notify);
        return true;
    }
}
