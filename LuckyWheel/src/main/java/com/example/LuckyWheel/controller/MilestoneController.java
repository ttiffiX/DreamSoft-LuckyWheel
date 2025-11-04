package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.controller.response.WheelInfoResponse;
import com.example.LuckyWheel.feature.user.manager.MilestoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/milestones")
@Slf4j
public class MilestoneController {
    private final MilestoneService milestoneService;

    @GetMapping()
    public ResponseEntity<List<WheelInfoResponse.MilestoneInfo>> getAvailableMilestones(
            @RequestParam String userId,
            @RequestParam Long wheelId) {
        List<WheelInfoResponse.MilestoneInfo> availableMilestones = milestoneService.getAvailableMilestones(userId, wheelId);
        return ResponseEntity.ok(availableMilestones);
    }

    @PostMapping()
    public ResponseEntity<List<WheelInfoResponse.RewardInfo>> claimMilestone(
            @RequestParam String userId,
            @RequestParam Long wheelId,
            @RequestParam Long milestoneId) {
        List<WheelInfoResponse.RewardInfo> rewards = milestoneService.claimMilestoneReward(userId, wheelId, milestoneId);
        return ResponseEntity.ok(rewards);
    }
}
