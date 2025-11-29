package com.example.LuckyWheel.feature.quest.manager;

import com.example.LuckyWheel.feature.quest.dto.QuestDTO;
import com.example.LuckyWheel.feature.quest.entity.UserQuestProgress;
import com.example.LuckyWheel.feature.quest.enums.QuestRequirementType;
import com.example.LuckyWheel.feature.quest.enums.QuestStatus;
import com.example.LuckyWheel.feature.quest.logic.QuestDataLoader;
import com.example.LuckyWheel.feature.quest.repository.UserQuestProgressRepository;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestServiceImpl implements QuestService {
    private final UserQuestProgressRepository questProgressRepository;
    private final QuestDataLoader questDataLoader;
    private final UserRepository userRepository;

    @Override
    public List<UserQuestProgress> getUserQuestProgress(String userId) {
        return questProgressRepository.findByUserId(userId);
    }

    @Override
    public UserQuestProgress getQuestProgressById(String userId, Long questId) {
        return questProgressRepository.findByUserIdAndInfoId(userId, questId)
                .orElse(null);
    }

    @Override
    @Transactional
    public UserQuestProgress initializeQuestForUser(String userId, Long questId) {
        UserQuestProgress existingProgress = questProgressRepository
                .findByUserIdAndInfoId(userId, questId).orElse(null);

        if (existingProgress != null) {
            return existingProgress;
        }

        QuestDTO questDTO = questDataLoader.getQuestById(questId);
        if (questDTO == null) {
            throw new RuntimeException("Quest configuration not found for ID: " + questId);
        }

        if (questProgressRepository.existsByUserIdAndStatus(userId, QuestStatus.ACTIVE.getValue())) {
            throw new RuntimeException("You must complete the current quest before starting a new one");
        }

        if (questDTO.getOrderIndex() > 1) {
            QuestDTO previousQuest = questDataLoader.getQuestByOrderIndex(questDTO.getOrderIndex() - 1);

            if (previousQuest != null) {
                boolean isPrevQuestClaimed = questProgressRepository
                        .findByUserIdAndInfoId(userId, previousQuest.getId())
                        .map(p -> p.getStatus().equals(QuestStatus.CLAIMED.getValue()))
                        .orElse(false);

                if (!isPrevQuestClaimed) {
                    throw new RuntimeException("You must complete the previous quest first");
                }
            }
        }

        Map<Long, Integer> initialProgress = new HashMap<>();
        questDTO.getRequirements().forEach(req ->
                initialProgress.put(req.getId(), 0)  // ← Sử dụng requirement.getId() làm key
        );

        UserQuestProgress progress = UserQuestProgress.builder()
                .id(userId + "_" + questId)
                .userId(userId)
                .infoId(questId)
                .status(QuestStatus.ACTIVE.getValue())
                .requirementProgress(initialProgress)
                .build();

        questProgressRepository.save(progress);
        log.info("Initialized quest {} for user {}", questId, userId);

        return progress;

    }

    @Override
    @Transactional
    public void updateProgress(String userId, QuestRequirementType type, Long targetId, Integer count) {
        // Lấy tất cả quest đang active của user
        List<UserQuestProgress> activeQuests = questProgressRepository
                .findByUserIdAndStatus(userId, QuestStatus.ACTIVE.getValue());

        for (UserQuestProgress progress : activeQuests) {
            QuestDTO questDTO = questDataLoader.getQuestById(progress.getInfoId());
            boolean progressUpdated = false;

            // Tìm requirement phù hợp với action
            for (QuestDTO.QuestRequirement requirement : questDTO.getRequirements()) {
                // Kiểm tra type có khớp không
                boolean typeMatches = requirement.getType().equals(type.getValue());

                // Kiểm tra targetId:
                // - Nếu targetId = 0 trong quest -> chấp nhận BẤT KỲ target nào
                // - Nếu targetId != 0 -> phải khớp chính xác
                boolean targetMatches = requirement.getTargetId().equals(0L) ||
                        requirement.getTargetId().equals(targetId);

                if (typeMatches && targetMatches) {
                    // Cập nhật progress
                    Integer currentProgress = progress.getRequirementProgress()
                            .getOrDefault(requirement.getId(), 0);
                    Integer newProgress = Math.min(currentProgress + count, requirement.getNumberRequire());

                    progress.getRequirementProgress().put(requirement.getId(), newProgress);
                    progressUpdated = true;

                    log.info("Updated quest {} requirement {} progress: {}/{} (type={}, targetId={}, anyTarget={})",
                            questDTO.getId(), requirement.getId(), newProgress, requirement.getNumberRequire(),
                            type.getName(), targetId, requirement.getTargetId().equals(0L));
                }
            }

            if (progressUpdated) {
                // Kiểm tra xem quest đã hoàn thành chưa
                if (checkAllRequirementsCompleted(questDTO, progress)) {
                    progress.setStatus(QuestStatus.COMPLETED.getValue());
                    progress.setCompletedAt(LocalDateTime.now());
                    log.info("Quest {} completed for user {}", questDTO.getId(), userId);
                }

                questProgressRepository.save(progress);
            }
        }

        log.info("Updated quest progress for user {}", userId);
    }

    @Override
    public boolean isQuestCompleted(String userId, Long questId) {
        return questProgressRepository.findByUserIdAndInfoId(userId, questId)
                .map(progress -> progress.getStatus().equals(QuestStatus.COMPLETED.getValue()))
                .orElse(false);
    }

    @Override
    @Transactional
    public UserQuestProgress claimReward(String userId, Long questId) {
        UserQuestProgress progress = questProgressRepository.findByUserIdAndInfoId(userId, questId)
                .orElseThrow(() -> new RuntimeException("Quest progress not found"));

        // Kiểm tra quest đã completed chưa
        if (!progress.getStatus().equals(QuestStatus.COMPLETED.getValue())) {
            throw new RuntimeException("Quest progress not completed");
        }

        // Lấy quest info để biết phần thưởng
        QuestDTO questDTO = questDataLoader.getQuestById(questId);

        // Thêm phần thưởng cho user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (QuestDTO.QuestReward reward : questDTO.getRewards()) {
            Integer currentAmount = user.getResources().getOrDefault(reward.getInfoId(), 0);
            user.getResources().put(reward.getInfoId(), currentAmount + reward.getNumber());
            log.info("Added reward {} x{} to user {}", reward.getInfoId(), reward.getNumber(), userId);
        }

        userRepository.save(user);

        // Đánh dấu quest là claimed
        progress.setStatus(QuestStatus.CLAIMED.getValue());
        questProgressRepository.save(progress);

        log.info("Successfully claimed reward for quest {} by user {}", questId, userId);

        // Tự động khởi tạo quest tiếp theo (nếu có)
        Long nextQuestId = questDataLoader.getQuestIdByOrderIndex(questDTO.getOrderIndex() + 1);
        if (nextQuestId != null) {
            initializeQuestForUser(userId, questDataLoader.getQuestIdByOrderIndex(questDTO.getOrderIndex() + 1));
        }

        return progress;
    }

    @Override
    public QuestDTO getQuestInfo(Long questId) {
        return questDataLoader.getQuestById(questId);
    }

    @Override
    public List<QuestDTO> getAllQuestInfo() {
        return questDataLoader.getAllQuestInfo();
    }

    /**
     * Helper method: Kiểm tra tất cả requirement đã hoàn thành chưa
     */
    private boolean checkAllRequirementsCompleted(QuestDTO questDTO, UserQuestProgress progress) {
        for (QuestDTO.QuestRequirement requirement : questDTO.getRequirements()) {
            Integer currentProgress = progress.getRequirementProgress()
                    .getOrDefault(requirement.getId(), 0);  // ← Sử dụng requirement.getId()
            if (currentProgress < requirement.getNumberRequire()) {
                return false;
            }
        }
        return true;
    }
}

