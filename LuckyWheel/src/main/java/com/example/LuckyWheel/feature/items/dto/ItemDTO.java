package com.example.LuckyWheel.feature.items.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Item từ items.json
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("itemType")
    private Integer itemType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("canTrade")
    private Integer canTrade;
}

