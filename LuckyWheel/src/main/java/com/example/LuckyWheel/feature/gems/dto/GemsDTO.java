package com.example.LuckyWheel.feature.gems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GemsDTO {
    private Long gemId;
    private String name;
    private String buffs;
}
