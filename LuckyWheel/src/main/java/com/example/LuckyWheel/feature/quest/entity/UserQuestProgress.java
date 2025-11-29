package com.example.LuckyWheel.feature.quest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_quest_progress")
public class UserQuestProgress {
    @Id
    private String id;
    private String userId;
    private Long infoId; //questId

    private Long status; // Active = 0, Completed = 1, Claimed = 2

    @Builder.Default
    private Map<Long, Integer> requirementProgress = new HashMap<>();

    private LocalDateTime completedAt;
}

