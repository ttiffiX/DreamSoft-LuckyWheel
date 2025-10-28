package com.example.LuckyWheel.feature.wheel.manager;

import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.repository.UserRepository;
import com.example.LuckyWheel.feature.wheel.entity.WheelMinestones;
import com.example.LuckyWheel.feature.wheel.logic.WheelDataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WheelServiceImpl implements  WheelService {
    private final UserRepository userRepository;
    private final WheelDataLoader wheelDataLoader;

//    @Override
//    public WheelMinestones getWheelMinestones(Long userId, Long wheelId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//
//        if (!wheelDataLoader.isActiveWheel(wheelId)) {
//            throw new RuntimeException("Wheel is not active");
//        }
//
//
//    }
}
