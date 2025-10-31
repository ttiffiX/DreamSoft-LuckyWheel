package com.example.LuckyWheel.feature.rewardhistory.entity;

import com.example.LuckyWheel.feature.user.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "reward_histories")
public class RewardHistory {
    @Id
    private String id;

    private String userId;
    private Long wheelId;

    private RewardInfo rewardInfo;

    @Builder.Default
    private LocalDateTime spinTime = LocalDateTime.now();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RewardInfo {
        private String itemId;
        private String itemName;
        private ResourceType itemType;
        private Integer quantity;
    }
}
