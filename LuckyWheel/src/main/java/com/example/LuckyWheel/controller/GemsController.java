package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.feature.gems.entity.Gems;
import com.example.LuckyWheel.feature.gems.manager.GemsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gems")
@Slf4j
public class GemsController {

    private final GemsService gemsService;

    @PostMapping("/add")
    public ResponseEntity<Gems> addGem(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Long gemId = Long.valueOf(request.get("gemId").toString());

        log.info("Adding gem {} to user {}", gemId, userId);
        Gems gem = gemsService.addGem(userId, gemId);
        return ResponseEntity.ok(gem);
    }

    @DeleteMapping("/{userId}/{gemInstanceId}")
    public ResponseEntity<Gems> removeGem(
            @PathVariable String gemInstanceId,
            @PathVariable String userId) {

        log.info("Removing gem instance {} from user {}", gemInstanceId, userId);
        Gems gem = gemsService.removeGem(userId, gemInstanceId);
        return ResponseEntity.ok(gem);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Gems>> getUserGems(@PathVariable String userId) {
        log.info("Getting all gems for user {}", userId);
        List<Gems> gems = gemsService.getUserGems(userId);
        return ResponseEntity.ok(gems);
    }

    @GetMapping("/user/{userId}/available")
    public ResponseEntity<List<Gems>> getAvailableGems(@PathVariable String userId) {
        log.info("Getting available gems for user {}", userId);
        List<Gems> gems = gemsService.getAvailableGems(userId);
        return ResponseEntity.ok(gems);
    }

}

