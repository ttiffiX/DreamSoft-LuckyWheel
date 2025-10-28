package com.example.LuckyWheel.feature.wheel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WheelConfigDTO {
    @JsonProperty("wheel")
    private List<WheelDTO> wheels;
}
