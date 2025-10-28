package com.example.LuckyWheel.feature.rewards.repository;

import com.example.LuckyWheel.feature.rewards.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardRepository extends JpaRepository<Reward,Long> {
}
