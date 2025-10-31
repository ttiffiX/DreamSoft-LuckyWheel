package com.example.LuckyWheel.feature.rewardhistory.repository;

import com.example.LuckyWheel.feature.rewardhistory.entity.RewardHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardHistoryRepository extends MongoRepository<RewardHistory, String> {
    Page<RewardHistory> findByWheelId(Long wheelId, Pageable pageable);
    Page<RewardHistory> findByWheelIdAndUserId(Long wheelId, String userId, Pageable pageable);
}
