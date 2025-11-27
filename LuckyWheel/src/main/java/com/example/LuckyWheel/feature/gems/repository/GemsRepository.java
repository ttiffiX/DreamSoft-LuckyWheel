package com.example.LuckyWheel.feature.gems.repository;

import com.example.LuckyWheel.feature.gems.entity.Gems;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GemsRepository extends MongoRepository<Gems, String> {
    List<Gems> findByUserId(String userId);
    Optional<Gems> findByIdAndUserId(String id, String userId);
    List<Gems> findByUserIdAndIsSocketed(String id, Boolean isSocketed);
}

