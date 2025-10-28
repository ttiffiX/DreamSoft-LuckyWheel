package com.example.LuckyWheel.feature.user.manager;

import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.TicketType;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {
    private UserRepository userRepository;

    @Override
    public boolean hasEnoughTickets(Long userId, String tickerType, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        TicketType ticketType = TicketType.fromValue(tickerType);
        int availableTickets = getTicketCount(user, ticketType);

        return availableTickets >= quantity;
    }

//    @Override
//    public void deductTickets(Long userId, String tickerType, int quantity) {
//
//    }

    private int getTicketCount(User user, TicketType ticketType) {
        return switch (ticketType) {
            case NORMAL -> user.getNormalTickets();
            case PREMIUM -> user.getPremiumTickets();
        };
    }

//    private void setTicketCount(User user, TicketType ticketType, int count) {
//        switch (ticketType) {
//            case NORMAL -> user.setNormalTickets(count);
//            case VIP -> user.setVipTickets(count);
//        }
//    }
}
