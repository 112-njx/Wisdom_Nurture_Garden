package com.Wisdom_Nurture_Garden.demo.service.impl;

import com.Wisdom_Nurture_Garden.demo.dao.BindingMapper;
import com.Wisdom_Nurture_Garden.demo.dao.UsersMapper;
import com.Wisdom_Nurture_Garden.demo.entity.Binding;
import com.Wisdom_Nurture_Garden.demo.entity.Users;
import com.Wisdom_Nurture_Garden.demo.service.BindingService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class BindingServiceImpl implements BindingService {

    @Autowired
    private BindingMapper bindingMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean bindElder(int childId, String elderName, String elderPassword) {
        //find the elder
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("name", elderName).eq("role",2);
        Users elder = usersMapper.selectOne(query);
        if (elder == null) return false;

        //ensure the password and check weather the elder has been bind
        String md5 = DigestUtils.md5DigestAsHex(elderPassword.getBytes());
        if (!md5.equals(elder.getPassword())) return false;

        QueryWrapper<Binding> check = new QueryWrapper<>();
        check.eq("child_id", childId);
        if (bindingMapper.selectOne(check) != null) return false;

        //绑定关系
        Binding binding = new Binding();
        binding.setChildId(childId);
        binding.setElderId(elder.getId());
        binding.setCreateTime(LocalDateTime.now());
        return bindingMapper.insert(binding) > 0;
    }

    @Override
    public Binding getBindingByChild(int childId) {
        QueryWrapper<Binding> query = new QueryWrapper<>();
        query.eq("child_id", childId);
        return bindingMapper.selectOne(query);
    }

    @Override
    public Binding getBindingByElder(int elderId) {
        QueryWrapper<Binding> query = new QueryWrapper<>();
        query.eq("elder_id", elderId);
        return bindingMapper.selectOne(query);
    }
}
