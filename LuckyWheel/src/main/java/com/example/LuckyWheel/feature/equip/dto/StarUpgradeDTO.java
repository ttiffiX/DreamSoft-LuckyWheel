package com.example.LuckyWheel.feature.equip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StarUpgradeDTO {
    private Integer starNumber;      // Số sao sẽ được nâng lên
    private Integer requiredEquipLevel;  // Level trang bị yêu cầu
    private Long costType;         // Loại phí (GOLD, DIAMOND, etc.)
    private Integer costAmount;      // Số lượng phí
    private Integer rate;            // Tỷ lệ thành công (%)
}

