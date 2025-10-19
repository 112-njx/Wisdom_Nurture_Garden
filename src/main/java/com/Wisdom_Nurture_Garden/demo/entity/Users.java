package com.Wisdom_Nurture_Garden.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("users")
public class Users {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private int gender;
    private String password;
    private String img;
    private Integer role;
    private String createTime;
    @TableField(value = "openid")
    private String openid;
    private String updateTime;

}
