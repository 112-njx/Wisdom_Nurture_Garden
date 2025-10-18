package com.Wisdom_Nurture_Garden.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("binding")
public class Binding {
    @TableId("id")
    private int id;
    @TableField("child_id")
    private int childId;
    @TableField("elder_id")
    private int elderId;
    @TableField("create_time")
    private LocalDateTime createTime;
}
