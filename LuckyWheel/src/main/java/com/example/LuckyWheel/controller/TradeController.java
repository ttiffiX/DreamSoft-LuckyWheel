package com.example.LuckyWheel.controller;


import com.example.LuckyWheel.controller.request.traderequest.CreateTradeRequest;
import com.example.LuckyWheel.controller.request.traderequest.TradeItemRequest;
import com.example.LuckyWheel.feature.trade.entity.Trade;
import com.example.LuckyWheel.feature.trade.manager.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
@Slf4j
public class TradeController {
    private final TradeService tradeService;

    @PostMapping()
    public ResponseEntity<Trade> createTrade(@RequestBody CreateTradeRequest createTradeRequest) {
        log.info("Creating trade between {} and {}", createTradeRequest.getInitUserId(), createTradeRequest.getPartnerUserId());
        Trade trade = tradeService.createTrade(createTradeRequest.getInitUserId(), createTradeRequest.getPartnerUserId());
        return ResponseEntity.ok(trade);
    }

    @GetMapping()
    public ResponseEntity<List<Trade>> getAllTrades(
            @RequestParam String userId) {
        log.info("Fetching trades for userId: {}", userId);
        List<Trade> trade = tradeService.getTradesForUser(userId);
        return ResponseEntity.ok(trade);
    }

    @GetMapping("/{tradeId}")
    public ResponseEntity<Trade> getTrades(@PathVariable String tradeId) {
        log.info("Fetching trades by tradeId: {}", tradeId);
        Trade trade = tradeService.getTradeById(tradeId);
        return ResponseEntity.ok(trade);
    }

    @PostMapping("/{tradeId}")
    public ResponseEntity<Trade> acceptTradeRequest(@PathVariable String tradeId, @RequestParam String partnerUserId) {
        log.info("Accept trade with id: {} by userId: {}", tradeId, partnerUserId);
        Trade trade = tradeService.acceptTradeRequest(tradeId, partnerUserId);
        return ResponseEntity.ok(trade);
    }

    @PostMapping("/{tradeId}/add-item")
    public ResponseEntity<Trade> addItemToTrade(
            @PathVariable String tradeId,
            @RequestBody TradeItemRequest tradeItemRequest) {
        log.info("Adding item {} (quantity: {}) to trade {} by user {}",
                tradeItemRequest.getItemId(),
                tradeItemRequest.getQuantity(),
                tradeId,
                tradeItemRequest.getUserId());
        Trade trade = tradeService.addItemToTrade(tradeId,
                tradeItemRequest.getUserId(),
                tradeItemRequest.getItemId(),
                tradeItemRequest.getQuantity());
        return ResponseEntity.ok(trade);
    }

    @PostMapping("/{tradeId}/remove-item")
    public ResponseEntity<Trade> removeItemFromTrade(
            @PathVariable String tradeId,
            @RequestBody TradeItemRequest tradeItemRequest) {
        log.info("Removing item {} from trade {} by user {}", tradeItemRequest.getItemId(), tradeId, tradeItemRequest.getUserId());
        Trade trade = tradeService.removeItemFromTrade(tradeId,
                tradeItemRequest.getUserId(), tradeItemRequest.getItemId());
        return ResponseEntity.ok(trade);
    }

    @PostMapping("/{tradeId}/accept")
    public ResponseEntity<Trade> acceptTrade(
            @PathVariable String tradeId,
            @RequestParam String userId) {
        log.info("User {} ready to trade {}", userId, tradeId);
        Trade trade = tradeService.acceptTrade(tradeId, userId);
        return ResponseEntity.ok(trade);
    }

    @PostMapping("/{tradeId}/cancel")
    public ResponseEntity<Trade> cancelTrade(
            @PathVariable String tradeId,
            @RequestParam String userId) {
        log.info("User {} cancelling trade {}", userId, tradeId);
        Trade trade = tradeService.cancelTrade(tradeId, userId);
        return ResponseEntity.ok(trade);
    }
}
