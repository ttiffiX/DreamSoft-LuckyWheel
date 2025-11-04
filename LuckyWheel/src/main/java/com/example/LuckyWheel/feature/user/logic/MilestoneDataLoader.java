package com.example.LuckyWheel.feature.user.logic;

import com.example.LuckyWheel.feature.user.dto.MilestoneDTO;
import com.example.LuckyWheel.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MilestoneDataLoader {
    private final ResourceLoader resourceLoader;
    private List<MilestoneDTO> milestoneList;

    public MilestoneDataLoader(
            @Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadMilestones() {
        try {
            var resource = resourceLoader.getResource("classpath:milestone.json");
            milestoneList = JsonUtils.parseJson(resource, new TypeReference<List<MilestoneDTO>>() {
            });
            log.info("Loaded {} milestone data", milestoneList.size());
        } catch (Exception e) {
            log.error("Failed to load milestone data", e);
            throw new RuntimeException("Cannot load milestone.json", e);
        }
    }

    public MilestoneDTO getMilestoneByWheelId(Long wheelId) {
        return milestoneList.stream()
                .filter(milestone -> milestone.getWheelId().equals(wheelId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Milestone not found for wheelId: " + wheelId));
    }

    public List<MilestoneDTO> getAllMilestones() {
        return milestoneList;
    }
}

