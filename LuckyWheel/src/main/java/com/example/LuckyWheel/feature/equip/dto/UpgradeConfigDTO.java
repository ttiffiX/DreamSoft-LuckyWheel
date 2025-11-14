package com.example.LuckyWheel.feature.equip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for equipment upgrade configuration
 * Loaded from upgrade_equip.json
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeConfigDTO {
    private Integer level;        // Target level after upgrade
    private Integer loseLevel;    // Level to drop to if upgrade fails
    private Integer rate;         // Success rate (0-100)
    private Integer increase;     // Stats increase percentage when upgrade succeeds
    private Long goldRequired;    // Gold cost for this upgrade
}

