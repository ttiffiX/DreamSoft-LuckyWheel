package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.user.enums.ResourceType;

import java.util.List;

public interface ResourceService {
    // Resource management
    void addResource(String username, ResourceType resourceType, int amount);
    void addResource(String username, List<SpinResultResponse> spinResultResponses);
    void updateMilestone(String username, Long wheelId, Long rewardId);

    void updateSpinCounts(String username, Long wheelId, int amount);

    void removeResource(String username, ResourceType resourceType, int amount);
    int getResourceAmount(String username, ResourceType resourceType);
    boolean hasEnoughResource(String username, ResourceType resourceType, int amount);
}
