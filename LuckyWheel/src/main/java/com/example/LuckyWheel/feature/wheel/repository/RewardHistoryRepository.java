package com.example.LuckyWheel.feature.wheel.repository;

import com.example.LuckyWheel.feature.wheel.entity.RewardHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardHistoryRepository extends JpaRepository<RewardHistory,Long> {
    Page<RewardHistory> findByWheelId(Long wheelId, Pageable pageable);
}
