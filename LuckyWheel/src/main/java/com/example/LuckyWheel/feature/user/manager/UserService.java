package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;

import java.util.List;
import java.util.Map;

public interface UserService {
    // User basic operations
    List<User> getAllUser();
    User getUserByName(String username);
    void createUser(User user);
    void updateUser(User user);

    // Resource management
    void addResource(String username, ResourceType resourceType, int amount);
    void addResource(String username, List<SpinResultResponse> spinResultResponses);

    void removeResource(String username, ResourceType resourceType, int amount);
    int getResourceAmount(String username, ResourceType resourceType);
    boolean hasEnoughResource(String username, ResourceType resourceType, int amount);

    Map<ResourceType, Integer> createInitialResources();
}
