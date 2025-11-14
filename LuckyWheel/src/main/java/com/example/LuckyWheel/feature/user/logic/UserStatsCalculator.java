package com.example.LuckyWheel.feature.user.logic;

import com.example.LuckyWheel.feature.equip.dto.EquipDTO;
import com.example.LuckyWheel.feature.equip.entity.Equip;
import com.example.LuckyWheel.feature.equip.logic.DataParser;
import com.example.LuckyWheel.feature.equip.logic.EquipItemDataLoader;
import com.example.LuckyWheel.feature.equip.manager.EquipService;
import com.example.LuckyWheel.feature.user.entity.User;
import com.example.LuckyWheel.feature.user.manager.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class UserStatsCalculator {
    private final EquipService equipService;
    private final EquipItemDataLoader equipItemDataLoader;
    private final DataParser dataParser;

    // Hàm tính toán Total Stats
    public Map<Long, Long> calculateTotalStats(User user) {
        Map<Long, Long> totalStats = new HashMap<>(user.getBaseStats());

        // 2. Lấy danh sách Item đang Equip (từ EquipService)
        List<Equip> equippedItems = equipService.getEquipByUserIdAndState(user.getId(), 1);

        // 3. Cộng dồn Modifiers
        for (Equip equip : equippedItems) {
            EquipDTO equipDTO = equipItemDataLoader.getEquipById(equip.getInfoId());
            Map<Long, Long> modifiers = dataParser.parseInfoBuff(equipDTO.getInfoBuff());


            for (Map.Entry<Long, Long> entry : modifiers.entrySet()) {

                // Định nghĩa các biến chính xác từ entry hiện tại
                Long statId = entry.getKey();
                Long modifierValue = entry.getValue();

                // Dùng phương thức Long::sum cho việc cộng đơn giản
                totalStats.merge(
                        statId,
                        modifierValue,
                        Long::sum
                );
            }
        }


        return totalStats;
    }
}
