package com.example.LuckyWheel.feature.equip.repository;

import com.example.LuckyWheel.feature.equip.entity.Equip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipRepository extends MongoRepository<Equip, String> {
    Optional<List<Equip>> findByUserId(String userId);
    Optional<List<Equip>> findByUserIdAndState(String userId, Integer state);
}
