package com.example.LuckyWheel.feature.user.entity;

import com.example.LuckyWheel.feature.user.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

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
    private Map<ResourceType, Integer> resources = new HashMap<>();
}
