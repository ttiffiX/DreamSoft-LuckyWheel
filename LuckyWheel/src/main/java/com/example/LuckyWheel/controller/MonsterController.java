package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.feature.monster.dto.CombatResultDTO;
import com.example.LuckyWheel.feature.monster.manager.MonsterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho Monster Combat
 */
@RestController
@RequestMapping("/monster")
@RequiredArgsConstructor
@Slf4j
public class MonsterController {

    private final MonsterService monsterService;

    @PostMapping("/challenge")
    public ResponseEntity<CombatResultDTO> challengeMonster(
            @RequestParam String userId,
            @RequestParam Long monsterId
    ) {
        log.info("Challenging monster. userId: {}, monsterId: {}", userId, monsterId);
        CombatResultDTO result = monsterService.challengeMonster(userId, monsterId);
        return ResponseEntity.ok(result);
    }

    /**
     * Challenge monster và trả về battle log dạng plain text (dễ đọc hơn)
     * Test: POST /monster/challenge/log?userId=1&monsterId=1
     */
    @PostMapping("/challenge/log")
    public ResponseEntity<String> challengeMonsterLog(
            @RequestParam String userId,
            @RequestParam Long monsterId
    ) {
        log.info("Challenging monster. userId: {}, monsterId: {}", userId, monsterId);
        CombatResultDTO result = monsterService.challengeMonster(userId, monsterId);
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=utf-8")
                .body(result.getBattleLog());
    }
}

