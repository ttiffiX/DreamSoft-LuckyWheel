package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.controller.request.SpinRequest;
import com.example.LuckyWheel.controller.response.SpinResultResponse;
import com.example.LuckyWheel.feature.wheel.manager.RewardHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reward-history")
@Slf4j
public class RewardHistoryController {
    private final RewardHistoryService rewardHistoryService;

    @GetMapping("/{wheelId}")
    public ResponseEntity<Page<SpinResultResponse>> getWheelSpinResponse(
            @PathVariable Long wheelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "spinTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        log.info("Get Wheel Spin Response - wheelId: {}, page: {}, size: {}", wheelId, page, size);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<SpinResultResponse> rewardHistory = rewardHistoryService.getRewardHistory(wheelId, pageable);
        return ResponseEntity.ok(rewardHistory);
    }

    @PostMapping()
    public ResponseEntity<List<SpinResultResponse>> saveSpinResult(@RequestBody SpinRequest request) {
        log.info("User {} spinning wheel {} with quantity {}", request.getUserId(), request.getWheelId(), request.getQuantity());
        List<SpinResultResponse> response = rewardHistoryService.saveRewardHistory(
                request.getUserId(),
                request.getWheelId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(response);
    }
}
