package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.feature.quest.dto.QuestDTO;
import com.example.LuckyWheel.feature.quest.entity.UserQuestProgress;
import com.example.LuckyWheel.feature.quest.manager.QuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/quests")
@Slf4j
public class QuestController {
    private final QuestService questService;

    /**
     * Lấy tất cả quest info (từ JSON)
     */
    @GetMapping()
    public ResponseEntity<List<QuestDTO>> getAllQuests() {
        log.info("Get all quest info");
        List<QuestDTO> quests = questService.getAllQuestInfo();
        return ResponseEntity.ok(quests);
    }

    /**
     * Lấy quest info theo ID
     */
    @GetMapping("/{questId}")
    public ResponseEntity<QuestDTO> getQuestById(@PathVariable Long questId) {
        log.info("Get quest {} info", questId);
        QuestDTO quest = questService.getQuestInfo(questId);
        return ResponseEntity.ok(quest);
    }

    /**
     * Lấy quest progress của user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserQuestProgress>> getUserQuestProgress(@PathVariable String userId) {
        log.info("Get quest progress for user {}", userId);
        List<UserQuestProgress> progress = questService.getUserQuestProgress(userId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Khởi tạo quest cho user
     */
    @PostMapping("/initialize")
    public ResponseEntity<UserQuestProgress> initializeQuest(@RequestParam String userId, @RequestParam Long questId) {
        log.info("Initialize quest {} for user {}", questId, userId);
        UserQuestProgress userQuestProgress = questService.initializeQuestForUser(userId, questId);
        return ResponseEntity.ok(userQuestProgress);
    }

    /**
     * Claim phần thưởng quest
     */
    @PostMapping("/{questId}/claim")
    public ResponseEntity<UserQuestProgress> claimReward(@PathVariable Long questId, @RequestParam String userId) {
        log.info("User {} claiming reward for quest {}", userId, questId);
        UserQuestProgress userQuestProgress = questService.claimReward(userId, questId);
        return ResponseEntity.ok(userQuestProgress);
    }

    /**
     * Kiểm tra quest đã hoàn thành chưa
     */
    @GetMapping("/{questId}/completed")
    public ResponseEntity<Boolean> isQuestCompleted(@PathVariable Long questId, @RequestParam String userId) {
        log.info("Check if quest {} is completed for user {}", questId, userId);
        boolean completed = questService.isQuestCompleted(userId, questId);
        return ResponseEntity.ok(completed);
    }
}

