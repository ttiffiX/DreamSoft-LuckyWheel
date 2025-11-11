package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.controller.response.WheelInfoResponse;
import com.example.LuckyWheel.feature.wheel.manager.WheelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/wheels")
@Slf4j
public class WheelController {
    private final WheelService wheelService;

    @GetMapping()
    public ResponseEntity<List<WheelInfoResponse>> getAllWheels() {
        log.info("Get All Wheel Info");
        List<WheelInfoResponse> wheels = wheelService.getAllWheelInfo();
        return ResponseEntity.ok(wheels);
    }

    @GetMapping("/{wheelId}")
    public ResponseEntity<WheelInfoResponse> getWheelById(@PathVariable Long wheelId) {
        log.info("Get Wheel {} Info, ", wheelId);
        WheelInfoResponse wheel = wheelService.getWheelInfo(wheelId);
        return ResponseEntity.ok(wheel);
    }
}


