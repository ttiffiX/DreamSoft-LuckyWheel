package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.feature.user.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    // User basic operations
    List<User> getAllUser();

    User getUserById(String userId);

    User getUserByName(String username);

    void createUser(User user);

    void updateUser(User user);

    Map<Long, Integer> createInitialResources();
}
