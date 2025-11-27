package com.example.LuckyWheel.feature.gems.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document("gems")
public class Gems {
    @Id
    private String id;

    private String userId;
    private Long gemId;

    private Boolean isSocketed;

}
