package com.example.LuckyWheel.feature.monster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO cho bảng loot của quái vật (monster_loot_table.json)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonsterLootTableDTO {
    @JsonProperty("monsterInfoId")
    private Long monsterInfoId;

    @JsonProperty("guaranteed")
    private Map<Long, Integer> guaranteed;  // Vật phẩm đảm bảo rơi (ví dụ: {"gold": 50})

    @JsonProperty("items")
    private List<LootItemDTO> items;  // Danh sách vật phẩm có thể rơi với tỷ lệ
}

