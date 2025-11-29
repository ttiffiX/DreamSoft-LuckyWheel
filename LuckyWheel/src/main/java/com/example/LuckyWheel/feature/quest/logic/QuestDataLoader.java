package com.example.LuckyWheel.feature.quest.logic;

import com.example.LuckyWheel.feature.quest.dto.QuestDTO;
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
public class QuestDataLoader {
    private final ResourceLoader resourceLoader;
    private List<QuestDTO> questDTOS;

    public QuestDataLoader(@Qualifier("webApplicationContext") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadQuests() {
        try {
            var resource = resourceLoader.getResource("classpath:quests.json");
            questDTOS = JsonUtils.parseJson(resource, new TypeReference<>() {
            });

            log.info("Loaded {} quest configurations", questDTOS.size());
        } catch (Exception e) {
            log.error("Failed to load quest config", e);
            throw new RuntimeException("Cannot load quests.json", e);
        }
    }

    public QuestDTO getQuestById(Long id) {
        return questDTOS.stream()
                .filter(quest -> quest.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Quest not found with id: " + id));
    }

    public List<QuestDTO> getAllQuestInfo() {
        return questDTOS;
    }

    public QuestDTO getQuestByOrderIndex(int orderIndex) {
        return questDTOS.stream()
                .filter(quest -> quest.getOrderIndex() == orderIndex)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Quest not found with order index: " + orderIndex));
    }

    public Long getQuestIdByOrderIndex(int orderIndex) {
        return questDTOS.stream()
                .filter(quest -> quest.getOrderIndex() == orderIndex)
                .findFirst()
                .map(QuestDTO::getId)
                .orElse(null);
    }
}

