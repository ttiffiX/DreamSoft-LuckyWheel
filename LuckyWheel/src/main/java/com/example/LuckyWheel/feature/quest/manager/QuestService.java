package com.example.LuckyWheel.feature.quest.manager;

import com.example.LuckyWheel.feature.quest.dto.QuestDTO;
import com.example.LuckyWheel.feature.quest.entity.UserQuestProgress;
import com.example.LuckyWheel.feature.quest.enums.QuestRequirementType;

import java.util.List;

public interface QuestService {
    UserQuestProgress getCurrentQuest(String userId);

    UserQuestProgress initializeQuestForUser(String userId, Long questId);

    void updateProgress(String userId, QuestRequirementType type, Long targetId, Integer count);

    boolean isQuestCompleted(String userId);

    UserQuestProgress claimReward(String userId);

    QuestDTO getQuestInfo(Long questId);

    List<QuestDTO> getAllQuestInfo();
}

