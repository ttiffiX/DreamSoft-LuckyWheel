package com.example.LuckyWheel.feature.quest.repository;

import com.example.LuckyWheel.feature.quest.entity.UserQuestProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuestProgressRepository extends MongoRepository<UserQuestProgress, String> {
    List<UserQuestProgress> findByUserId(String userId);

    Optional<UserQuestProgress> findByUserIdAndInfoId(String userId, Long questId);

    boolean existsByUserIdAndInfoId(String userId, Long questId);

    List<UserQuestProgress> findByUserIdAndStatus(String userId, Long status);

    boolean existsByUserIdAndStatus(String userId, Long status);
}

