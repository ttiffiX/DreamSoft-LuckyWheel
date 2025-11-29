package com.example.LuckyWheel.feature.quest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("requiredLevel")
    private Integer requiredLevel;

    @JsonProperty("orderIndex")
    private Integer orderIndex;

    @JsonProperty("requirements")
    private List<QuestRequirement> requirements;

    @JsonProperty("rewards")
    private List<QuestReward> rewards;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestRequirement {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("des")
        private String description;

        @JsonProperty("type")
        private Long type;

        @JsonProperty("targetId")
        private Long targetId;

        @JsonProperty("numberRequire")
        private Integer numberRequire;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestReward {
        @JsonProperty("infoId")
        private Long infoId;

        @JsonProperty("number")
        private Integer number;
    }
}

