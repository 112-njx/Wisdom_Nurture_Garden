package com.Wisdom_Nurture_Garden.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("help_request")
public class HelpRequest {
    @TableId("id")
    private int id;
    private int elderId;
    private int childId;
    private LocalDateTime createTime;
}
