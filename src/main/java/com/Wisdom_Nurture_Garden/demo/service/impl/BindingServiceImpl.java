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

        System.out.println("绑定流程开始");
        System.out.println("子女ID：" + childId + "，要绑定的老人用户名：" + elderName);

        // 验证子女账号是否存在
        Users child = usersMapper.selectById(childId);
        if (child == null || child.getRole() != 1) {
            System.out.println("子女账户不存在或角色错误");
            return false;
        }

        // 查找老人账户
        QueryWrapper<Users> elderQuery = new QueryWrapper<>();
        elderQuery.eq("name", elderName).eq("role", 2);
        Users elder = usersMapper.selectOne(elderQuery);

        if (elder == null) {
            System.out.println("老人账户不存在");
            return false;
        }

        // 验证老人密码
        String md5 = DigestUtils.md5DigestAsHex(elderPassword.getBytes());
        if (!md5.equals(elder.getPassword())) {
            System.out.println("老人密码错误");
            return false;
        }

        // 检查子女是否已经绑定
        QueryWrapper<Binding> check = new QueryWrapper<>();
        check.eq("child_id", childId);
        if (bindingMapper.selectOne(check) != null) {
            System.out.println("该子女已经绑定过老人");
            return false;
        }

        // 检查该老人是否已经被绑定（防止多子女绑定同一老人）
        QueryWrapper<Binding> checkElder = new QueryWrapper<>();
        checkElder.eq("elder_id", elder.getId());
        if (bindingMapper.selectOne(checkElder) != null) {
            System.out.println("该老人已经被其他子女绑定");
            return false;
        }

        // 创建绑定关系
        Binding binding = new Binding();
        binding.setChildId(childId);
        binding.setElderId(elder.getId());
        binding.setCreateTime(LocalDateTime.now());

        int result = bindingMapper.insert(binding);

        if (result > 0) {
            System.out.println("绑定成功！子女：" + child.getName() + " <-> 老人：" + elder.getName());
            return true;
        } else {
            System.out.println("插入绑定失败");
            return false;
        }
    }

    @Override
    public Binding getBindingByChild(int childId) {
        QueryWrapper<Binding> query = new QueryWrapper<>();
        query.eq("child_id", childId);
        Binding binding = bindingMapper.selectOne(query);

        if (binding != null) {
            System.out.println("查询绑定成功：child_id=" + childId + " -> elder_id=" + binding.getElderId());
        } else {
            System.out.println("该子女暂无绑定记录：child_id=" + childId);
        }

        return binding;
    }

    @Override
    public Binding getBindingByElder(int elderId) {
        QueryWrapper<Binding> query = new QueryWrapper<>();
        query.eq("elder_id", elderId);
        Binding binding = bindingMapper.selectOne(query);

        if (binding != null) {
            System.out.println("查询绑定成功：elder_id=" + elderId + " -> child_id=" + binding.getChildId());
        } else {
            System.out.println("该老人暂无绑定记录：elder_id=" + elderId);
        }

        return binding;
    }
}
