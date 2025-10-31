package com.example.LuckyWheel.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ResourceRequest {
    private String username;
    private int resourceType;
    private int amount;
}
