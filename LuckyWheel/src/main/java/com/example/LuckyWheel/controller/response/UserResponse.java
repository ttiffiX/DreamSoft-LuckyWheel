package com.example.LuckyWheel.controller.response;

import com.example.LuckyWheel.feature.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserResponse {
    User user;
    Map<Long, Long> stats;
}
