package com.example.LuckyWheel.feature.equip.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document("equips")
public class Equip {
    @Id
    private String id;

    private String userId;
    private Long infoId;
    private Integer state;  // 0 - trong túi đồ, 1 - đang trang bị
    private Integer level; // cấp độ trang bị
    private Integer star; // số lỗ đã đục

    private Map<Long, Long> propsMain;
    private List<Long> listGemIds;
}
