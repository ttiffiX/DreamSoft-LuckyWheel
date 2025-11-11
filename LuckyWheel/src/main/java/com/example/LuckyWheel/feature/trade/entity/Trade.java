package com.example.LuckyWheel.feature.trade.entity;

import com.example.LuckyWheel.feature.trade.enums.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Trade {
    @Id
    private String id;

    private String initUserId;
    private String partnerUserId;

    private Map<Long, tradeItem> initUserItems;
    private Map<Long, tradeItem> partnerUserItems;

    private TradeStatus status;
    private boolean initAccepted;
    private boolean partnerAccepted;


    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class tradeItem {
//        private String itemId;
        private String itemName;
        private Long itemType;
        private Integer quantity;
    }
}
