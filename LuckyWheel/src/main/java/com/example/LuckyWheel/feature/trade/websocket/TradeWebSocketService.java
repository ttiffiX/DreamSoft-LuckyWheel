package com.example.LuckyWheel.feature.trade.websocket;

import com.example.LuckyWheel.feature.trade.entity.Trade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending WebSocket notifications to trade participants
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TradeWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send trade update to BOTH users in the trade
     *
     * @param trade     Trade data
     * @param eventType Event type (e.g., "ITEM_ADDED", "TRADE_LOCKED")
     */
    public void notifyTradeUpdate(Trade trade, String eventType) {
        TradeUpdateMessage message = TradeUpdateMessage.builder()
                .eventType(eventType)
                .trade(trade)
                .timestamp(System.currentTimeMillis())
                .build();

        // Send to init user
        sendToUser(trade.getInitUserId(), message);

        // Send to partner user
        sendToUser(trade.getPartnerUserId(), message);
    }

    /**
     * Send notification to a SPECIFIC user only
     *
     * @param userId    User ID to send notification to
     * @param eventType Event type
     * @param trade     Trade data
     */
    public void notifyUser(String userId, String eventType, Trade trade) {
        TradeUpdateMessage message = TradeUpdateMessage.builder()
                .eventType(eventType)
                .trade(trade)
                .timestamp(System.currentTimeMillis())
                .build();

        sendToUser(userId, message);
    }

    /**
     * Send notification to a SPECIFIC user with custom message
     *
     * @param userId        User ID
     * @param eventType     Event type
     * @param trade         Trade data
     * @param customMessage Custom message
     */
    public void notifyUser(String userId, String eventType, Trade trade, String customMessage) {
        TradeUpdateMessage message = TradeUpdateMessage.builder()
                .eventType(eventType)
                .trade(trade)
                .timestamp(System.currentTimeMillis())
                .message(customMessage)
                .build();

        sendToUser(userId, message);
    }

    /**
     * Internal method to send message to a user
     */
    private void sendToUser(String userId, TradeUpdateMessage message) {
        try {

            // FIX: Use /topic instead of /user because we don't have Spring Security Principal
            // Pattern: /topic/user-{userId}/trade-updates
            String destination = "/topic/user-" + userId + "/trade-updates";
            // Send to topic destination directly
            messagingTemplate.convertAndSend(destination, message);

        } catch (Exception e) {
            log.error("❌ Failed to send WebSocket message to user {}: {}",
                    userId, e.getMessage());
        }
    }
}

