package com.example.LuckyWheel.controller.response;

import com.example.LuckyWheel.feature.equip.entity.Equip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EquipResponse {
    private Equip equip;
    private Map<Long, Long> propsMain;
}
