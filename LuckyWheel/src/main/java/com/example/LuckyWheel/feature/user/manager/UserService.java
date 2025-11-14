package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.controller.response.UserResponse;
import com.example.LuckyWheel.feature.user.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    // User basic operations
    List<User> getAllUser();

    User getUserEntityById(String userId);

    UserResponse getUserById(String userId);

    UserResponse getUserByName(String username);

    void createUser(User user);

    void updateUser(User user);

    Map<Long, Integer> createInitialResources();
}
