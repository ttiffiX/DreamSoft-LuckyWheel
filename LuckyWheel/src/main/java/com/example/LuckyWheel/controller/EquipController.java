package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.controller.response.EquipResponse;
import com.example.LuckyWheel.feature.equip.entity.Equip;
import com.example.LuckyWheel.feature.equip.manager.EquipActionService;
import com.example.LuckyWheel.feature.equip.manager.EquipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equips")
@RequiredArgsConstructor
@Slf4j
public class EquipController {
    private final EquipService equipService;
    private final EquipActionService equipActionService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<EquipResponse>> getUserEquips(@PathVariable String userId) {
        log.info("getUserEquips with userId: {}", userId);
        List<EquipResponse> userEquips = equipService.getEquipByUserId(userId);
        return ResponseEntity.ok(userEquips);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Equip> addEquipToUser(@PathVariable String userId, @RequestParam Long equipInfoId) {
        log.info("addEquipToUser with userId: {}, equipInfoId: {}", userId, equipInfoId);
        Equip newEquip = equipService.addEquipToUser(userId, equipInfoId);
        return ResponseEntity.ok(newEquip);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Equip> removeEquip(@PathVariable String userId, @RequestParam String equipId) {
        log.info("removeEquip with userId: {}, equipId: {}", userId, equipId);
        equipService.removeEquip(userId, equipId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/state/{userId}")
    public ResponseEntity<Equip> equipItemToUser(@PathVariable String userId,
                                                 @RequestParam String equipId,
                                                 @RequestParam int state) {
        log.info("Change state to {} with userId: {}, equipId: {}", state, userId, equipId);
        Equip equippedItem = equipService.changeStateEquip(userId, equipId, state);
        return ResponseEntity.ok(equippedItem);
    }

    @PutMapping("/upgrade/{userId}")
    public ResponseEntity<Equip> upgradeEquipment(@PathVariable String userId, @RequestParam String equipId) {
        log.info("upgradeEquipment with userId: {}, equipId: {}", userId, equipId);
        Equip result = equipActionService.upgradeEquipment(userId, equipId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/star/{userId}")
    public ResponseEntity<Equip> upgradeStarEquip(@PathVariable String userId, @RequestParam String equipId) {
        log.info("upgradeStarEquip with userId: {}, equipId: {}", userId, equipId);
        var result = equipActionService.upgradeStarEquip(userId, equipId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/gem/{userId}")
    public ResponseEntity<Equip> socketGem(@PathVariable String userId,
                                           @RequestParam String equipId,
                                           @RequestParam String gemInstanceId,
                                           @RequestParam boolean action) {
        log.info("equip/unequip gem - userId: {}, equipId: {}, gemInstanceId: {}", userId, equipId, gemInstanceId);
        Equip result = equipActionService.changeStateGemToEquip(userId, equipId, gemInstanceId, action);
        return ResponseEntity.ok(result);
    }

}
