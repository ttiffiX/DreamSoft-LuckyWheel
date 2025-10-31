//package com.example.LuckyWheel.feature.wheel.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder(toBuilder = true)
//@Entity
//@Table(
//        name = "wheel_milestones"
//)
//public class WheelMinestones {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long userId;
//    private Long wheelId;
//    private Integer spin_count;
//    private LocalDateTime last_milestone_claim_time;
//}
