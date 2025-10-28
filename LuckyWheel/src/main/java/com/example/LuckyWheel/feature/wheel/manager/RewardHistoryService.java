package com.example.LuckyWheel.feature.wheel.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RewardHistoryService {
    Page<SpinResultResponse> getRewardHistory(Long wheelId, Pageable pageable);
    List<SpinResultResponse> saveRewardHistory(Long userId, Long wheelId, int quantity);

}
