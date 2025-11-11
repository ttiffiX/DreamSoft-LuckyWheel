package com.example.LuckyWheel.feature.trade.websocket;

import com.example.LuckyWheel.feature.trade.entity.Trade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Trade WebSocket update messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeUpdateMessage {

    /**
     * Event type (e.g., "TRADE_CREATED", "ITEM_ADDED")
     */
    private String eventType;

    /**
     * Trade data
     */
    private Trade trade;

    /**
     * Unix timestamp (milliseconds)
     */
    private Long timestamp;

    /**
     * Optional message for the user
     */
    private String message;
}

