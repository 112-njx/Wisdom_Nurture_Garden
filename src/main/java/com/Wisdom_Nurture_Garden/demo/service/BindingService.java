package com.Wisdom_Nurture_Garden.demo.service;

import com.Wisdom_Nurture_Garden.demo.entity.Binding;

public interface BindingService {
    boolean bindElder(int childId, String elderName, String elderPassword);
    Binding getBindingByChild(int childId);
    Binding getBindingByElder(int elderId);
}
