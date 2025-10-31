package com.example.LuckyWheel.feature.rewardhistory.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RewardHistoryService {
    Page<SpinResultResponse> getRewardHistory(Long wheelId, String username, Pageable pageable);
    List<SpinResultResponse> saveRewardHistory(String username, Long wheelId, int quantity);

}
