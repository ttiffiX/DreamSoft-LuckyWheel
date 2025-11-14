package com.example.LuckyWheel.feature.trade.manager;

import com.example.LuckyWheel.feature.items.dto.ItemDTO;
import com.example.LuckyWheel.feature.items.logic.ItemDataLoader;
import com.example.LuckyWheel.feature.trade.entity.Trade;
import com.example.LuckyWheel.feature.trade.enums.TradeStatus;
import com.example.LuckyWheel.feature.trade.websocket.TradeEventType;
import com.example.LuckyWheel.feature.trade.websocket.TradeWebSocketService;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.manager.ResourceService;
import com.example.LuckyWheel.feature.user.manager.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {
    private final UserService userService;
    private final ConcurrentHashMap<String, Trade> tradeCache;
    private final ItemDataLoader itemDataLoader;
    private final TradeWebSocketService webSocketService;  // ← THÊM WebSocket Service

    private static final String TRADE_KEY_PREFIX = "trade:";
    private final ResourceService resourceService;


    @Override
    public Trade createTrade(String initUserId, String partnerUserId) {
        // Validate: 2 user phải khác nhau
        if (initUserId.equals(partnerUserId)) {
            throw new IllegalArgumentException("Cannot create trade with yourself");
        }

        // Kiểm tra cả 2 user có tồn tại không
        userService.getUserEntityById(initUserId);
        userService.getUserEntityById(partnerUserId);


        // Tạo trade mới
//        String tradeId = UUID.randomUUID().toString();
        String tradeId = "1";
        Trade trade = Trade.builder()
                .id(tradeId)
                .initUserId(initUserId)
                .partnerUserId(partnerUserId)
                .initUserItems(new LinkedHashMap<>())
                .partnerUserItems(new LinkedHashMap<>())
                .status(TradeStatus.PENDING)
//                .initLocked(false)
//                .partnerLocked(false)
                .initAccepted(false)
                .partnerAccepted(false)
                .createdAt(LocalDateTime.now())
                .build();

        saveTrade(trade);
        log.info("Trade created with ID: {}", trade.getId());

        // WebSocket: Notify partner user về trade request mới
        webSocketService.notifyUser(partnerUserId, TradeEventType.TRADE_CREATED, trade);

        return trade;
    }

    @Override
    public List<Trade> getTradesForUser(String userId) {
        List<Trade> userTrades = new ArrayList<>();

        for (Trade trade : tradeCache.values()) {
            if (trade.getInitUserId().equals(userId) || trade.getPartnerUserId().equals(userId)) {
                userTrades.add(trade);
            }
        }

        return userTrades;
    }

    @Override
    public Trade acceptTradeRequest(String tradeId, String partnerUserId) {
        Trade trade = getTradeById(tradeId);
        if (!trade.getPartnerUserId().equals(partnerUserId)) {
            throw new IllegalArgumentException("Partner user ID does not match the trade");
        }

        if (!trade.getStatus().equals(TradeStatus.PENDING)) {
            throw new IllegalStateException("Only pending trades can be accepted");
        }

        trade.setStatus(TradeStatus.ACTIVE);

        saveTrade(trade);

        log.info("Trade accepted with ID: {}", tradeId);

        // WebSocket: Notify init user về partner đã accept
        webSocketService.notifyUser(trade.getInitUserId(), TradeEventType.TRADE_ACCEPTED, trade);

        return trade;
    }


    @Override
    public Trade getTradeById(String tradeId) {
        String tradeKey = TRADE_KEY_PREFIX + tradeId;
        Trade trade = tradeCache.get(tradeKey);

        if (trade == null) {
            throw new RuntimeException("Trade not found with ID: " + tradeId);
        }

        return trade;
    }

    @Override
    public Trade addItemToTrade(String tradeId, String userId, Long itemId, Integer quantity) {
        Trade trade = getTradeById(tradeId);

        // Validate: Trade phải ở trạng thái ACTIVE
        validateTradeActive(trade);

        // Validate: quantity phải > 0
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Xác định user là init hay partner và lấy list items tương ứng
        Map<Long, Trade.tradeItem> userItems = getUserItemsMap(trade, userId);

        trade.setPartnerAccepted(false);
        trade.setInitAccepted(false);

        ItemDTO item = itemDataLoader.getItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + itemId);
        }
        if (item.getCanTrade() != 1) { // Đã kiểm tra null ở trên
            throw new IllegalArgumentException("Item cannot be traded: " + itemId);
        }

        User user = userService.getUserEntityById(userId);

        int userItemQuantity = user.getResources().getOrDefault(itemId, 0);
        int currentTradeQuantity = userItems.containsKey(itemId) ? userItems.get(itemId).getQuantity() : 0;

        if (currentTradeQuantity + quantity > userItemQuantity) {
            throw new IllegalArgumentException("Not enough item quantity in user's inventory for item: " + itemId);
        }

        // Update hoặc thêm mới
        if (userItems.containsKey(itemId)) {
            Trade.tradeItem existingItem = userItems.get(itemId);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            Trade.tradeItem newItem = Trade.tradeItem.builder()
                    .itemName(item.getName())
                    .itemType(item.getItemType())
                    .quantity(quantity)
                    .build();
            userItems.put(itemId, newItem);
        }

        saveTrade(trade);
        log.info("Item {} added to trade {} by user {}", itemId, tradeId, userId);

        // WebSocket: Notify cả 2 users về item được thêm
        webSocketService.notifyTradeUpdate(trade, TradeEventType.ITEM_ADDED);

        return trade;
    }

    @Override
    public Trade removeItemFromTrade(String tradeId, String userId, Long itemId) {
        Trade trade = getTradeById(tradeId);

        // Validate: Trade phải ở trạng thái ACTIVE
        validateTradeActive(trade);

        // Xác định user là init hay partner và lấy list items tương ứng
        Map<Long, Trade.tradeItem> userItems = getUserItemsMap(trade, userId);
        trade.setPartnerAccepted(false);
        trade.setInitAccepted(false);

        if (!userItems.containsKey(itemId)) {
            throw new IllegalArgumentException("Item not found in trade: " + itemId);
        }

        // Remove item khỏi trade
        userItems.remove(itemId);

        saveTrade(trade);
        log.info("Item {} removed from trade {} by user {}", itemId, tradeId, userId);

        webSocketService.notifyTradeUpdate(trade, TradeEventType.ITEM_REMOVED);
        return trade;
    }

//    @Override
//    public Trade lockTrade(String tradeId, String userId) {
//        Trade trade = getTradeById(tradeId);
//        validateTradeActive(trade);
//
//        if (trade.getInitUserId().equals(userId)) {
//            trade.setInitLocked(true);
//        } else if (trade.getPartnerUserId().equals(userId)) {
//            trade.setPartnerLocked(true);
//        } else {
//            throw new IllegalArgumentException("User is not part of this trade");
//        }
//
//        saveTrade(trade);
//        log.info("User {} locked trade {}", userId, tradeId);
//
//        return trade;
//    }

//    @Override
//    public Trade unlockTrade(String tradeId, String userId) {
//        Trade trade = getTradeById(tradeId);
//        validateTradeActive(trade);
//
//        // Xác định user và unlock
//        if (trade.getInitUserId().equals(userId)) {
//            if (!trade.isInitLocked()) {
//                throw new IllegalStateException("Trade is not locked by this user");
//            }
//            trade.setInitLocked(false);
//            // Reset accept khi unlock
//            trade.setInitAccepted(false);
//            trade.setPartnerAccepted(false);
//        } else if (trade.getPartnerUserId().equals(userId)) {
//            if (!trade.isPartnerLocked()) {
//                throw new IllegalStateException("Trade is not locked by this user");
//            }
//            trade.setPartnerLocked(false);
//            // Reset accept khi unlock
//            trade.setPartnerAccepted(false);
//            trade.setInitAccepted(false);
//        } else {
//            throw new IllegalArgumentException("User is not part of this trade");
//        }
//
//        saveTrade(trade);
//        log.info("User {} unlocked trade {}", userId, tradeId);
//
//        return trade;
//    }

    @Override
    public Trade acceptTrade(String tradeId, String userId) {
        Trade trade = getTradeById(tradeId);
        validateTradeActive(trade);

//        if(!(trade.isInitLocked() && trade.isPartnerLocked())) {
//            throw new IllegalStateException("Both users must lock the trade before accepting");
//        }

        // Xác định user và accept
        if (trade.getInitUserId().equals(userId)) {
            trade.setInitAccepted(true);
        } else if (trade.getPartnerUserId().equals(userId)) {
            trade.setPartnerAccepted(true);
        } else {
            throw new IllegalArgumentException("User is not part of this trade");
        }

        saveTrade(trade);
        log.info("User {} accepted trade {}", userId, tradeId);

        // Kiểm tra xem cả 2 đã accept chưa, nếu rồi thì có thể complete
        if (trade.isInitAccepted() && trade.isPartnerAccepted()) {
            completeTrade(tradeId);
        } else {
            webSocketService.notifyTradeUpdate(trade, TradeEventType.TRADE_CONFIRMED);
        }

        return trade;
    }

//    @Override
//    public Trade unacceptTrade(String tradeId, String userId) {
//        Trade trade = getTradeById(tradeId);
//        validateTradeActive(trade);
//
//        // Xác định user và unaccept
//        if (trade.getInitUserId().equals(userId)) {
//            if (!trade.isInitAccepted()) {
//                throw new IllegalStateException("Trade is not accepted by this user");
//            }
//            trade.setInitAccepted(false);
//            trade.setPartnerAccepted(false);
//        } else if (trade.getPartnerUserId().equals(userId)) {
//            if (!trade.isPartnerAccepted()) {
//                throw new IllegalStateException("Trade is not accepted by this user");
//            }
//            trade.setPartnerAccepted(false);
//            trade.setInitAccepted(false);
//        } else {
//            throw new IllegalArgumentException("User is not part of this trade");
//        }
//
//        saveTrade(trade);
//        log.info("User {} unaccepted trade {}", userId, tradeId);
//
//        return trade;
//    }

    @Override
    public void completeTrade(String tradeId) {
        Trade trade = getTradeById(tradeId);
        validateTradeActive(trade);

        // Validate: Cả 2 users phải đã accept
        if (!trade.isInitAccepted() || !trade.isPartnerAccepted()) {
            throw new IllegalStateException("Both users must accept the trade before completing");
        }

        // Swap items giữa 2 users
        User initUser = userService.getUserEntityById(trade.getInitUserId());
        User partnerUser = userService.getUserEntityById(trade.getPartnerUserId());

        // Trừ items của init user và cộng cho partner user
        Map<Long, Trade.tradeItem> initItems = trade.getInitUserItems();
        for (Map.Entry<Long, Trade.tradeItem> entry : initItems.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue().getQuantity();

            // Trừ từ init user
            Integer initUserCurrentQty = initUser.getResources().getOrDefault(itemId, 0);
            if (initUserCurrentQty < quantity) {
                throw new IllegalStateException("Init user doesn't have enough items to complete trade");
            }
            initUser.getResources().put(itemId, initUserCurrentQty - quantity);

            // Cộng cho partner user
            Integer partnerUserCurrentQty = partnerUser.getResources().getOrDefault(itemId, 0);
            partnerUser.getResources().put(itemId, partnerUserCurrentQty + quantity);
        }

        // Trừ items của partner user và cộng cho init user
        Map<Long, Trade.tradeItem> partnerItems = trade.getPartnerUserItems();
        for (Map.Entry<Long, Trade.tradeItem> entry : partnerItems.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue().getQuantity();

            // Trừ từ partner user
            Integer partnerUserCurrentQty = partnerUser.getResources().getOrDefault(itemId, 0);
            if (partnerUserCurrentQty < quantity) {
                throw new IllegalStateException("Partner user doesn't have enough items to complete trade");
            }
            partnerUser.getResources().put(itemId, partnerUserCurrentQty - quantity);

            // Cộng cho init user
            Integer initUserCurrentQty = initUser.getResources().getOrDefault(itemId, 0);
            initUser.getResources().put(itemId, initUserCurrentQty + quantity);
        }

        // Lưu lại users vào database
        resourceService.updateResource(initUser);
        resourceService.updateResource(partnerUser);

        // Đổi status trade thành COMPLETED
        trade.setStatus(TradeStatus.COMPLETED);

        webSocketService.notifyTradeUpdate(trade, TradeEventType.TRADE_COMPLETED);

        // Xóa trade khỏi cache (đã hoàn thành, không cần lưu nữa)
        String tradeKey = TRADE_KEY_PREFIX + tradeId;
        tradeCache.remove(tradeKey);

        log.info("Trade {} completed successfully. Items swapped between {} and {}",
                tradeId, trade.getInitUserId(), trade.getPartnerUserId());

    }

    @Override
    public Trade cancelTrade(String tradeId, String userId) {
        Trade trade = getTradeById(tradeId);

        // Validate: User phải thuộc trade
        if (!trade.getInitUserId().equals(userId) && !trade.getPartnerUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not part of this trade");
        }

        trade.setStatus(TradeStatus.CANCELLED);

        webSocketService.notifyTradeUpdate(trade, TradeEventType.TRADE_CANCELLED);

        // Xóa trade khỏi cache (đã cancel, không cần lưu nữa)
        String tradeKey = TRADE_KEY_PREFIX + tradeId;
        tradeCache.remove(tradeKey);

        log.info("Trade {} cancelled by user {}", tradeId, userId);

        return trade;
    }

    private void saveTrade(Trade trade) {
        String tradeKey = TRADE_KEY_PREFIX + trade.getId();
        tradeCache.put(tradeKey, trade);
    }


    private void validateTradeActive(Trade trade) {
        if (!trade.getStatus().equals(TradeStatus.ACTIVE)) {
            throw new IllegalStateException("Can only modify items in active trades");
        }
    }

    private Map<Long, Trade.tradeItem> getUserItemsMap(Trade trade, String userId) {
        Map<Long, Trade.tradeItem> userItems;
//        boolean isUserLocked;

        if (trade.getInitUserId().equals(userId)) {
//            isUserLocked = trade.isInitLocked();
            userItems = trade.getInitUserItems();
        } else if (trade.getPartnerUserId().equals(userId)) {
//            isUserLocked = trade.isPartnerLocked();
            userItems = trade.getPartnerUserItems();
        } else {
            throw new IllegalArgumentException("User is not part of this trade");
        }

        return userItems;
    }


}
