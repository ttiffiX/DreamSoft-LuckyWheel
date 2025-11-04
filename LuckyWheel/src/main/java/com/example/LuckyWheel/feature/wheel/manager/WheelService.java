package com.example.LuckyWheel.feature.wheel.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.controller.response.WheelInfoResponse;

import java.util.List;

public interface WheelService {
    List<SpinResultResponse> spin(Long wheelId, int quantity);
    // Wheel spin count management (for milestone rewards)
    void incrementSpinCount(String userId, Long wheelId, int count);
    Integer getSpinCount(String userId, Long wheelId);

    // Get wheel information with milestones
    WheelInfoResponse getWheelInfo(Long wheelId);
    List<WheelInfoResponse> getAllWheelInfo();
}

