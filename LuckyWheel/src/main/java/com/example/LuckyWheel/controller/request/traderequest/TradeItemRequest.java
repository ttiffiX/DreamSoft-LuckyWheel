package com.example.LuckyWheel.controller.request.traderequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TradeItemRequest {
    private String userId;
    private Long itemId;
    private Integer quantity;
}
