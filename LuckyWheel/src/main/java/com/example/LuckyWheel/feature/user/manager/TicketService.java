package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.TicketType;

public interface TicketService {
    boolean hasEnoughTickets(Long userId, String tickerType, int quantity);
//    void deductTickets(Long userId, String tickerType, int quantity);
}
