package com.example.LuckyWheel.config;

import com.example.LuckyWheel.feature.trade.entity.Trade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {

    @Bean
    public ConcurrentHashMap<String, Trade> tradeCache() {
        return new ConcurrentHashMap<>();
    }
}

