package com.example.LuckyWheel.feature.trade.websocket;

/**
 * Constants for Trade WebSocket event types
 */
public class TradeEventType {

    // Trade request events
    public static final String TRADE_CREATED = "TRADE_CREATED";
    public static final String TRADE_ACCEPTED = "TRADE_ACCEPTED";

    // Item management events
    public static final String ITEM_ADDED = "ITEM_ADDED";
    public static final String ITEM_REMOVED = "ITEM_REMOVED";

    // Trade locking events
//    public static final String TRADE_LOCKED = "TRADE_LOCKED";
//    public static final String TRADE_UNLOCKED = "TRADE_UNLOCKED";

    // Trade confirmation events
    public static final String TRADE_CONFIRMED = "TRADE_CONFIRMED";
    public static final String TRADE_UNCONFIRMED = "TRADE_UNCONFIRMED";

    // Trade completion events
    public static final String TRADE_COMPLETED = "TRADE_COMPLETED";
    public static final String TRADE_CANCELLED = "TRADE_CANCELLED";

    private TradeEventType() {
        // Private constructor to prevent instantiation
    }
}

