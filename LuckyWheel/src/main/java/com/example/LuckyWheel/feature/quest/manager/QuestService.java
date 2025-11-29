package com.example.LuckyWheel.feature.quest.manager;

import com.example.LuckyWheel.feature.quest.dto.QuestDTO;
import com.example.LuckyWheel.feature.quest.entity.UserQuestProgress;
import com.example.LuckyWheel.feature.quest.enums.QuestRequirementType;

import java.util.List;

public interface QuestService {
    List<UserQuestProgress> getUserQuestProgress(String userId);

    UserQuestProgress getQuestProgressById(String userId, Long questId);

    UserQuestProgress initializeQuestForUser(String userId, Long questId);

    void updateProgress(String userId, QuestRequirementType type, Long targetId, Integer count);

    boolean isQuestCompleted(String userId, Long questId);

    UserQuestProgress claimReward(String userId, Long questId);

    QuestDTO getQuestInfo(Long questId);

    List<QuestDTO> getAllQuestInfo();
}

