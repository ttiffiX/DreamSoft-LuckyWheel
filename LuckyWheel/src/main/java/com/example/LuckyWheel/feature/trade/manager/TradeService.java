package com.example.LuckyWheel.feature.trade.manager;

import com.example.LuckyWheel.feature.trade.entity.Trade;

import java.util.List;

public interface TradeService {
    // Tạo phiên giao dịch mới
    Trade createTrade(String initUserId, String partnerUserId);

    List<Trade> getTradesForUser(String userId);

    //Partner đồng ý
    Trade acceptTradeRequest(String tradeId, String partnerUserId);

    // Lấy thông tin giao dịch theo ID
    Trade getTradeById(String tradeId);

    // Thêm item vào giao dịch (cho init user hoặc partner user)
    Trade addItemToTrade(String tradeId, String userId, Long itemId, Integer quantity);

    // Xóa item khỏi giao dịch
    Trade removeItemFromTrade(String tradeId, String userId, Long itemId);

    // Khóa giao dịch (người dùng xác nhận không thay đổi item nữa)
//    Trade lockTrade(String tradeId, String userId);

    // Mở khóa giao dịch (người dùng muốn thay đổi item)
//    Trade unlockTrade(String tradeId, String userId);

    // Chấp nhận giao dịch (sau khi đã lock)
    Trade acceptTrade(String tradeId, String userId);

    // Hủy chấp nhận giao dịch
//    Trade unacceptTrade(String tradeId, String userId);

    // Hoàn tất giao dịch (khi cả 2 đều accept và lock)
    void completeTrade(String tradeId);

    // Hủy giao dịch
    Trade cancelTrade(String tradeId, String userId);

}
