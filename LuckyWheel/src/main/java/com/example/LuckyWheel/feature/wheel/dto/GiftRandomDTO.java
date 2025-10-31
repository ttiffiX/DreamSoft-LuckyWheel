package com.example.LuckyWheel.feature.wheel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftRandomDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("infoId")
    private Long infoId;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("probability")
    private int probability;
}
