package com.example.LuckyWheel.feature.items.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserItemDTO {
    private Long itemId;
    private String name;
    private Long itemType;
    private String description;
    private Integer quantity;
    private Integer canTrade; // 0 = cannot trade, 1 = can trade
}

