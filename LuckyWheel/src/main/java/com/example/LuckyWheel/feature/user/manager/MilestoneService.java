package com.example.LuckyWheel.feature.user.manager;


import com.example.LuckyWheel.controller.response.WheelInfoResponse;

import java.util.List;

public interface MilestoneService {

    // Check available milestones that can be claimed
    List<WheelInfoResponse.MilestoneInfo> getAvailableMilestones(String userId, Long wheelId);

    // Claim milestone reward
    List<WheelInfoResponse.RewardInfo> claimMilestoneReward(String userId, Long wheelId, Long milestoneId);
}
