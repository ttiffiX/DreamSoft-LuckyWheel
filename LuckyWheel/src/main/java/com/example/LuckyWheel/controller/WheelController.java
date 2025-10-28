package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.feature.wheel.dto.WheelDTO;
import com.example.LuckyWheel.feature.wheel.logic.WheelDataLoader;
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
    private final WheelDataLoader wheelDataLoader;

    @GetMapping()
    public ResponseEntity<List<WheelDTO>> getAllWheels() {
        log.info("Get all wheels");
        List<WheelDTO> wheels = wheelDataLoader.getAllWheels();
        return ResponseEntity.ok(wheels);
    }

    @GetMapping("/{wheelId}")
    public ResponseEntity<WheelDTO> getWheelById(@PathVariable Long wheelId) {
        log.info("Get wheel by id: {}", wheelId);
        WheelDTO wheel = wheelDataLoader.getWheelById(wheelId);
        return ResponseEntity.ok(wheel);
    }
}

