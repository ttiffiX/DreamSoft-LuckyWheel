package com.example.LuckyWheel.feature.inventory.repository;

import com.example.LuckyWheel.feature.inventory.entity.InventoryItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends MongoRepository<InventoryItem, String> {

    // Tìm inventory của user (mỗi user chỉ có 1 inventory)
    Optional<InventoryItem> findByUserId(String userId);

    // Xóa inventory của user
    void deleteByUserId(String userId);
}

