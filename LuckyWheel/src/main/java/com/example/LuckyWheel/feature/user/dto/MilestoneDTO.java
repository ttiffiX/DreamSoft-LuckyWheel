package com.example.LuckyWheel.feature.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("wheelId")
    private Long wheelId;

    @JsonProperty("milestoneRewards")
    private List<MilestoneReward> milestoneRewards;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MilestoneReward {
        @JsonProperty("milestone")
        private Integer milestone;

        @JsonProperty("id")
        private Long id;

        @JsonProperty("rewards")
        private List<Reward> rewards;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reward {
        @JsonProperty("infoId")
        private Long infoId;

        @JsonProperty("number")
        private Integer number;
    }
}
