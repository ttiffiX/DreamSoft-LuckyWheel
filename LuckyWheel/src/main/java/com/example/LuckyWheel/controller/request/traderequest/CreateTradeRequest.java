package com.example.LuckyWheel.controller.request.traderequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateTradeRequest {
    private String initUserId;
    private String partnerUserId;
}

