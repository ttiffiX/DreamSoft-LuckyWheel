package com.example.LuckyWheel.feature.wheel.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;

import java.util.List;

public interface WheelService {
    List<SpinResultResponse> spin(Long wheelId, int quantity);
}
