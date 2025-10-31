package com.example.LuckyWheel.controller.response;

import com.example.LuckyWheel.feature.user.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SpinResultResponse {
    private String rewardId;
    private String rewardName;
    private ResourceType rewardType;
    private Integer quantity;
    private LocalDateTime spinTime;
}
