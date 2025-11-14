package com.example.LuckyWheel.feature.equip.logic;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataParser {
    public Map<Long, Long> parseInfoBuff(String infoBuff) {
        // Khởi tạo Map để lưu kết quả
        Map<Long, Long> buffMap = new HashMap<>();

        // Kiểm tra chuỗi rỗng hoặc null
        if (infoBuff == null || infoBuff.trim().isEmpty()) {
            return buffMap;
        }

        // 1. Tách chuỗi thành các cặp key-value riêng biệt dựa trên dấu chấm phẩy (;)
        String[] pairs = infoBuff.split(";");

        // 2. Lặp qua từng cặp
        for (String pair : pairs) {
            // Loại bỏ khoảng trắng thừa
            String trimmedPair = pair.trim();

            // 3. Tách Key và Value dựa trên dấu gạch ngang (-)
            if (trimmedPair.contains("-")) {
                String[] parts = trimmedPair.split("-");

                // Đảm bảo có đúng 2 phần
                if (parts.length == 2) {
                    try {
                        // Chuyển đổi sang kiểu Long
                        Long key = Long.parseLong(parts[0].trim());
                        Long value = Long.parseLong(parts[1].trim());

                        // Thêm vào Map
                        buffMap.put(key, value);

                    } catch (NumberFormatException e) {
                        // Log lỗi nếu key hoặc value không phải là số hợp lệ
                        throw new RuntimeException("Invalid number format in infoBuff: " + trimmedPair, e);

                    }
                }
            }
        }

        return buffMap;
    }
}