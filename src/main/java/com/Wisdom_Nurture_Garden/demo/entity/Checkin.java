package com.Wisdom_Nurture_Garden.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("checkin")
public class Checkin {
    @TableId("id")
    private int id;
    private int elderId;
    private int mood;
    private int sleepQuality;
    private int appetite;
    private LocalDateTime createTime;
}
