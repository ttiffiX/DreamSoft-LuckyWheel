package com.example.LuckyWheel.feature.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String username;
    private String password;

    // Map lưu số lượng tài nguyên (ResourceType -> Số lượng)
    // GOLD: vàng (dùng để mua vé NORMAL)
    // DIAMOND: kim cương (dùng để mua vé PREMIUM)
    @Builder.Default
    private Map<Long, Integer> resources = new HashMap<>();

    // Map đếm số lần spin theo wheelId (wheelId -> count)
    // Dùng để check milestone rewards (10, 20, 30 lần)
    @Builder.Default
    private Map<Long, Integer> wheelSpinCounts = new HashMap<>();

    @Builder.Default
    private Map<Long, Set<Long>> milestoneRewardsClaimed = new HashMap<>();
}
