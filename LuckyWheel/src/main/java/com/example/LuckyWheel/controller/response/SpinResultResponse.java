package com.example.LuckyWheel.controller.response;

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
    private Long rewardId;
    private String rewardName;
    private String rewardType;
    private String ticketType;
    private Integer quantity;
    private LocalDateTime spinTime;
}
