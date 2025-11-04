package com.example.LuckyWheel.controller;

import com.example.LuckyWheel.controller.request.ResourceRequest;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.enums.ResourceType;
import com.example.LuckyWheel.feature.user.manager.ResourceService;
import com.example.LuckyWheel.feature.user.manager.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final ResourceService resourceService;

    @GetMapping()
    public ResponseEntity<List<User>> getAllUser() {
        log.info("Get All User");
        List<User> user = userService.getAllUser();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        log.info("Get User by Username: {}", username);
        User user = userService.getUserByName(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    public ResponseEntity<String> createUser(@RequestBody User user) {
        log.info("Create User");
        userService.createUser(user);
        return ResponseEntity.ok("Created user successfully.");
    }

    @PostMapping("/resource")
    public ResponseEntity<String> addResource(@RequestBody ResourceRequest resourceRequest) {
        log.info("Add Resource to User: {}, ResourceType: {}, Amount: {}", resourceRequest.getUsername(), resourceRequest.getResourceType(), resourceRequest.getResourceType());
        ResourceType resourceType = ResourceType.fromValue(resourceRequest.getResourceType());
        resourceService.addResource(resourceRequest.getUsername(), resourceType, resourceRequest.getAmount());
        return ResponseEntity.ok("Added resource successfully.");
    }
}
