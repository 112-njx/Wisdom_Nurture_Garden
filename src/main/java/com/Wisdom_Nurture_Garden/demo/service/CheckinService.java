package com.Wisdom_Nurture_Garden.demo.service;

import com.Wisdom_Nurture_Garden.demo.entity.Checkin;
import java.util.List;

public interface CheckinService {
    boolean submitCheckin(Checkin checkin);
    List<Checkin> getTodayCheckinByElder(Integer elderId);
}
