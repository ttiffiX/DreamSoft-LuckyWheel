package com.example.LuckyWheel.controller.response;

import com.example.LuckyWheel.feature.user.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WheelInfoResponse {
    private Long wheelId;
    private String wheelName;
    private boolean isActive;
    private ResourceType resourceType;
    private List<GiftInfo> gifts;
    private List<MilestoneInfo> milestones;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftInfo {
        private Long id;
        private String itemName; // Tên item từ items.json
        private Integer number;
        private Integer probability;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MilestoneInfo {
        private Long id;
        private Integer milestone;
        private List<RewardInfo> rewards;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardInfo {
        private String itemName;
        private Integer number;
    }
}

