# 📋 PHÂN TÍCH STRUCTURE - HỆ THỐNG TRANG BỊ (EQUIP SYSTEM)

## 🎯 TỔNG QUAN HỆ THỐNG HIỆN TẠI

### 1. Entity Structure

#### Equip Entity (Vật phẩm cụ thể của user)
```java
@Document("equips")
public class Equip {
    private String id;              // MongoDB ObjectId - unique cho mỗi equip instance
    private String userId;          // User sở hữu
    private Long infoId;            // Link đến EquipDTO (equip.json) - thông tin template
    private Integer state;          // 0 = trong túi, 1 = đang trang bị
    private Integer level;          // Cấp độ hiện tại (1 -> maxLevel)
    private Integer star;           // Số gem đã gắn (0 -> maxStar)
    
    Map<Long, Long> propsMain;      // Chỉ số chính (statId -> value)
    List<Long> listGemIds;          // Danh sách gem đã gắn
}
```

#### EquipDTO (Template từ equip.json)
```java
public class EquipDTO {
    private Long id;                // 3001, 3002, ... (template ID)
    private String name;            // "Kiếm Sandai Kitesu"
    private Long type;              // 3000 (loại trang bị: vũ khí, giáp, ...)
    private String maxStar;         // Max gem có thể gắn (8)
    private String maxLevel;        // Max level có thể nâng (16)
    private String infoBuff;        // "3-1000;5-20" (statId-value)
}
```

#### User Entity (Đã có baseStats)
```java
private Map<Long, Long> baseStats = {
    HP: 1000,
    MP: 500,
    ATTACK: 100,
    DEFENSE: 50,
    SPEED: 10
};
```

---

## 🏗️ STRUCTURE ĐỀ XUẤT

### Phân tầng theo chức năng:

```
equip/
├── entity/
│   └── Equip.java                          // MongoDB document
│
├── dto/
│   ├── EquipDTO.java                       // Template từ JSON
│   ├── EquipInstanceDTO.java               // Response cho FE (equip + template merged)
│   └── UserStatsDTO.java                   // Tổng hợp stats (base + equip)
│
├── repository/
│   └── EquipRepository.java                // MongoDB queries
│
├── logic/
│   ├── EquipItemDataLoader.java           // Load equip.json
│   ├── EquipStatsCalculator.java          // Tính toán stats từ equip
│   └── EquipValidator.java                 // Validate rules (max level, max star, etc.)
│
├── manager/
│   ├── EquipService.java                   // Interface
│   └── EquipServiceImpl.java               // Core business logic
│
└── enums/
    ├── EquipType.java                      // WEAPON, ARMOR, ACCESSORY
    ├── EquipSlot.java                      // HEAD, BODY, WEAPON, etc.
    └── EquipState.java                     // IN_INVENTORY, EQUIPPED
```

---

## 🎮 CÁC HÀNH ĐỘNG CẦN XỬ LÝ

### 1. ✅ ADD EQUIP (User nhận đồ mới)

**Khi nào:**
- Quay wheel nhận được equip
- Mua từ shop
- Nhận từ event/quest

**Xử lý ở:**
- **EquipServiceImpl.addEquipToUser()**

**Logic:**
```java
public Equip addEquipToUser(String userId, Long equipInfoId) {
    // 1. Validate: equipInfoId có tồn tại trong equip.json?
    EquipDTO equipInfo = equipDataLoader.getEquipById(equipInfoId);
    if (equipInfo == null) throw new NotFoundException();
    
    // 2. Tạo Equip instance mới
    Equip newEquip = Equip.builder()
        .userId(userId)
        .infoId(equipInfoId)
        .state(0)                           // Mặc định trong túi
        .level(1)                           // Level 1
        .star(0)                            // Chưa gắn gem
        .propsMain(parseInitialProps())     // Parse từ infoBuff
        .listGemIds(new ArrayList<>())      // Empty
        .build();
    
    // 3. Save vào DB
    return equipRepository.save(newEquip);
    
    // 4. (Optional) Notify user qua WebSocket
}
```

**Lưu ý:**
- Mỗi lần add tạo 1 **instance mới** (unique ID)
- User có thể có nhiều equip cùng infoId (ví dụ 3 cái Kiếm Sandai Kitesu)

---

### 2. ✅ REMOVE EQUIP (Bán/Xóa đồ)

**Khi nào:**
- User bán equip
- Phân rã equip để lấy tài nguyên
- Admin xóa

**Xử lý ở:**
- **EquipServiceImpl.removeEquip()**

**Logic:**
```java
public void removeEquip(String equipId, String userId) {
    // 1. Get equip
    Equip equip = equipRepository.findById(equipId)
        .orElseThrow(() -> new NotFoundException());
    
    // 2. Validate ownership
    if (!equip.getUserId().equals(userId)) {
        throw new UnauthorizedException();
    }
    
    // 3. Check state
    if (equip.getState() == 1) {
        throw new IllegalStateException("Cannot remove equipped item. Unequip first!");
    }
    
    // 4. Delete
    equipRepository.delete(equip);
    
    // 5. (Optional) Return resources to user (nếu phân rã)
    // userService.addResources(userId, calculatedResources);
}
```

---

### 3. ✅ EQUIP (Trang bị đồ - Thay đổi state 0 → 1)

**Khi nào:**
- User click "Trang bị"

**Xử lý ở:**
- **EquipServiceImpl.equipItem()**

**Logic:**
```java
public Equip equipItem(String equipId, String userId) {
    // 1. Get equip
    Equip equip = equipRepository.findById(equipId)
        .orElseThrow(() -> new NotFoundException());
    
    // 2. Validate
    if (!equip.getUserId().equals(userId)) throw new UnauthorizedException();
    if (equip.getState() == 1) throw new IllegalStateException("Already equipped");
    
    // 3. Check slot conflict
    EquipDTO equipInfo = equipDataLoader.getEquipById(equip.getInfoId());
    EquipType type = EquipType.fromValue(equipInfo.getType());
    
    // 3.1. Tìm equip đang trang bị cùng slot (nếu có)
    List<Equip> currentEquipped = equipRepository.findByUserIdAndStateAndType(
        userId, 1, type
    );
    
    // 3.2. Auto unequip old item (hoặc throw error nếu muốn user tháo thủ công)
    if (!currentEquipped.isEmpty()) {
        for (Equip old : currentEquipped) {
            old.setState(0);
            equipRepository.save(old);
        }
    }
    
    // 4. Equip new item
    equip.setState(1);
    Equip updated = equipRepository.save(equip);
    
    // 5. Recalculate user stats
    recalculateUserStats(userId);
    
    // 6. Return updated equip
    return updated;
}
```

**Quan trọng:**
- **Một slot chỉ trang bị 1 item** (ví dụ: 1 vũ khí, 1 giáp, ...)
- Khi equip mới → Auto unequip cũ (hoặc yêu cầu tháo trước)

---

### 4. ✅ UNEQUIP (Tháo đồ - Thay đổi state 1 → 0)

**Khi nào:**
- User click "Tháo"
- Auto unequip khi equip item mới cùng slot

**Xử lý ở:**
- **EquipServiceImpl.unequipItem()**

**Logic:**
```java
public Equip unequipItem(String equipId, String userId) {
    // 1. Get equip
    Equip equip = equipRepository.findById(equipId)
        .orElseThrow(() -> new NotFoundException());
    
    // 2. Validate
    if (!equip.getUserId().equals(userId)) throw new UnauthorizedException();
    if (equip.getState() == 0) throw new IllegalStateException("Not equipped");
    
    // 3. Unequip
    equip.setState(0);
    Equip updated = equipRepository.save(equip);
    
    // 4. Recalculate user stats
    recalculateUserStats(userId);
    
    // 5. Return
    return updated;
}
```

---

### 5. ⏳ UPGRADE LEVEL (Nâng cấp - Chưa làm)

**Xử lý ở:**
- **EquipServiceImpl.upgradeLevel()**

**Logic đề xuất:**
```java
public Equip upgradeLevel(String equipId, String userId, Map<Long, Integer> materials) {
    // 1. Get equip
    Equip equip = equipRepository.findById(equipId).orElseThrow();
    
    // 2. Validate
    if (!equip.getUserId().equals(userId)) throw new UnauthorizedException();
    
    EquipDTO equipInfo = equipDataLoader.getEquipById(equip.getInfoId());
    int maxLevel = Integer.parseInt(equipInfo.getMaxLevel());
    
    if (equip.getLevel() >= maxLevel) {
        throw new IllegalStateException("Max level reached");
    }
    
    // 3. Validate materials (check user resources)
    EquipValidator.validateUpgradeMaterials(userId, equip.getLevel(), materials);
    
    // 4. Consume materials
    userService.consumeResources(userId, materials);
    
    // 5. Upgrade level
    equip.setLevel(equip.getLevel() + 1);
    
    // 6. Recalculate propsMain (stats tăng theo level)
    Map<Long, Long> newProps = EquipStatsCalculator.calculatePropsForLevel(
        equipInfo.getInfoBuff(), 
        equip.getLevel()
    );
    equip.setPropsMain(newProps);
    
    // 7. Save
    Equip updated = equipRepository.save(equip);
    
    // 8. Recalculate user stats (nếu đang equip)
    if (equip.getState() == 1) {
        recalculateUserStats(userId);
    }
    
    return updated;
}
```

**Nên xử lý ở:**
- **Logic layer**: `EquipStatsCalculator.calculatePropsForLevel()`
- **Service layer**: Validate, consume resources, save
- **Repository**: Chỉ CRUD

---

### 6. ⏳ ENHANCE STAR (Khảm gem - Chưa làm)

**Xử lý ở:**
- **EquipServiceImpl.enhanceStar()**

**Logic đề xuất:**
```java
public Equip enhanceStar(String equipId, String userId, Long gemId) {
    // 1. Get equip
    Equip equip = equipRepository.findById(equipId).orElseThrow();
    
    // 2. Validate
    if (!equip.getUserId().equals(userId)) throw new UnauthorizedException();
    
    EquipDTO equipInfo = equipDataLoader.getEquipById(equip.getInfoId());
    int maxStar = Integer.parseInt(equipInfo.getMaxStar());
    
    if (equip.getStar() >= maxStar) {
        throw new IllegalStateException("Max star reached");
    }
    
    // 3. Check user has gem
    if (!userService.hasGem(userId, gemId)) {
        throw new InsufficientResourceException("No gem");
    }
    
    // 4. Consume gem
    userService.consumeGem(userId, gemId);
    
    // 5. Add gem to equip
    equip.getListGemIds().add(gemId);
    equip.setStar(equip.getStar() + 1);
    
    // 6. Recalculate stats (gem adds bonus)
    // Gem có thể thêm stats mới hoặc tăng stats hiện có
    Map<Long, Long> gemStats = gemDataLoader.getGemStats(gemId);
    Map<Long, Long> updatedProps = mergeStats(equip.getPropsMain(), gemStats);
    equip.setPropsMain(updatedProps);
    
    // 7. Save
    Equip updated = equipRepository.save(equip);
    
    // 8. Recalculate user stats
    if (equip.getState() == 1) {
        recalculateUserStats(userId);
    }
    
    return updated;
}
```

**Nên xử lý ở:**
- **Logic layer**: `GemDataLoader`, `EquipStatsCalculator`
- **Service layer**: Business logic, validation

---

## 📊 TÍNH TOÁN STATS

### Công thức tổng stats của User:

```
totalStats = baseStats + equippedStats + buffStats

Trong đó:
- baseStats: User.baseStats (HP: 1000, ATTACK: 100, ...)
- equippedStats: Tổng stats từ TẤT CẢ equip đang trang bị (state = 1)
- buffStats: Từ skill, potion, ... (nếu có)
```

### Xử lý ở:
- **EquipStatsCalculator.calculateUserStats()**

**Logic:**
```java
public class EquipStatsCalculator {
    
    public static UserStatsDTO calculateUserStats(String userId) {
        // 1. Get base stats
        User user = userService.getUserById(userId);
        Map<Long, Long> totalStats = new HashMap<>(user.getBaseStats());
        
        // 2. Get all equipped items
        List<Equip> equippedItems = equipRepository.findByUserIdAndState(userId, 1);
        
        // 3. Sum stats from equips
        for (Equip equip : equippedItems) {
            Map<Long, Long> equipStats = equip.getPropsMain();
            for (Map.Entry<Long, Long> entry : equipStats.entrySet()) {
                totalStats.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }
        
        // 4. Return DTO
        return UserStatsDTO.builder()
            .userId(userId)
            .baseStats(user.getBaseStats())
            .equipStats(calculateEquipStatsSum(equippedItems))
            .totalStats(totalStats)
            .build();
    }
}
```

**Gọi khi nào:**
- Sau khi equip/unequip
- Sau khi upgrade level
- Sau khi enhance star
- Khi FE request stats (GET /users/{userId}/stats)

---

## 🎯 REPOSITORY METHODS CẦN THIẾT

```java
public interface EquipRepository extends MongoRepository<Equip, String> {
    
    // Get all equips của 1 user
    List<Equip> findByUserId(String userId);
    
    // Get equips theo state (0 = túi, 1 = trang bị)
    List<Equip> findByUserIdAndState(String userId, Integer state);
    
    // Get equip đang trang bị theo type (để check conflict)
    List<Equip> findByUserIdAndStateAndInfoId(String userId, Integer state, Long infoId);
    
    // Count equips trong túi
    long countByUserIdAndState(String userId, Integer state);
    
    // Delete all equips của user (khi delete user)
    void deleteByUserId(String userId);
}
```

---

## 🔄 FLOW HOÀN CHỈNH

### Scenario: User quay wheel nhận được equip

```
1. WheelService.spin()
   ↓
2. Result: Equip reward (infoId: 3001)
   ↓
3. EquipService.addEquipToUser(userId, 3001)
   ↓
4. Create Equip instance:
   - id: "equip_abc123" (MongoDB ID)
   - userId: "user123"
   - infoId: 3001 (Kiếm Sandai Kitesu)
   - state: 0 (trong túi)
   - level: 1
   - star: 0
   - propsMain: {ATTACK: 1000, SPEED: 20} (parse từ infoBuff)
   - listGemIds: []
   ↓
5. Save to equips collection
   ↓
6. Return to FE: EquipInstanceDTO
   {
     id: "equip_abc123",
     name: "Kiếm Sandai Kitesu",
     type: 3000,
     state: 0,
     level: 1,
     maxLevel: 16,
     star: 0,
     maxStar: 8,
     propsMain: {ATTACK: 1000, SPEED: 20}
   }
```

### Scenario: User trang bị equip

```
1. User click "Trang bị" on equip_abc123
   ↓
2. EquipService.equipItem("equip_abc123", "user123")
   ↓
3. Check slot conflict:
   - equipInfo.type = 3000 (WEAPON)
   - Find current equipped WEAPON → equip_xyz789
   ↓
4. Auto unequip old:
   - equip_xyz789.state = 1 → 0
   ↓
5. Equip new:
   - equip_abc123.state = 0 → 1
   ↓
6. Recalculate user stats:
   - baseStats: {HP: 1000, ATTACK: 100, ...}
   - equippedStats: {ATTACK: 1000, SPEED: 20} (từ Kiếm)
   - totalStats: {HP: 1000, ATTACK: 1100, SPEED: 20, ...}
   ↓
7. Return UserStatsDTO to FE
```

---

## 📁 FILES CẦN TẠO/SỬA

### Cần tạo mới:

1. **EquipInstanceDTO.java** - Response cho FE (merge equip + equipInfo)
2. **UserStatsDTO.java** - Tổng hợp stats
3. **EquipStatsCalculator.java** - Logic tính toán stats
4. **EquipValidator.java** - Validate rules
5. **EquipController.java** - REST endpoints
6. **EquipType.java** - Enum loại equip
7. **EquipSlot.java** - Enum vị trí trang bị
8. **EquipState.java** - Enum trạng thái (0, 1)

### Cần sửa:

1. **EquipService.java** - Thêm methods
2. **EquipServiceImpl.java** - Implement logic
3. **EquipRepository.java** - Thêm query methods
4. **User.java** - (Đã có baseStats, OK)

---

## 🎨 API ENDPOINTS ĐỀ XUẤT

```
GET    /equips/user/{userId}              # Get all equips (túi + đang equip)
GET    /equips/user/{userId}/inventory    # Get equips trong túi (state=0)
GET    /equips/user/{userId}/equipped     # Get equips đang trang bị (state=1)
GET    /equips/{equipId}                  # Get equip detail

POST   /equips/user/{userId}              # Add equip (from wheel/shop)
       Body: { infoId: 3001 }

DELETE /equips/{equipId}                  # Remove equip

PUT    /equips/{equipId}/equip            # Trang bị
PUT    /equips/{equipId}/unequip          # Tháo

PUT    /equips/{equipId}/upgrade          # Nâng cấp level
       Body: { materials: {1: 100, 2: 50} }

PUT    /equips/{equipId}/enhance          # Khảm gem
       Body: { gemId: 5001 }

GET    /users/{userId}/stats              # Get tổng stats (base + equip)
```

---

## ✅ KẾT LUẬN & KHUYẾN NGHỊ

### Structure tối ưu:

1. **Entity (Equip)**: Lưu instance cụ thể, state, level, star
2. **DTO (EquipDTO)**: Template từ JSON (không đổi)
3. **Logic Layer**: Tính toán stats, validate rules
4. **Service Layer**: Business logic (equip, unequip, upgrade, enhance)
5. **Repository**: CRUD + custom queries

### Xử lý ở đâu:

| Action | Layer | Component |
|--------|-------|-----------|
| Add equip | Service | EquipServiceImpl.addEquipToUser() |
| Remove equip | Service | EquipServiceImpl.removeEquip() |
| Equip item | Service | EquipServiceImpl.equipItem() |
| Unequip item | Service | EquipServiceImpl.unequipItem() |
| Upgrade level | Service | EquipServiceImpl.upgradeLevel() |
| Enhance star | Service | EquipServiceImpl.enhanceStar() |
| Calculate stats | Logic | EquipStatsCalculator.calculateUserStats() |
| Validate rules | Logic | EquipValidator.validate*() |
| Parse infoBuff | Logic | EquipStatsCalculator.parseInfoBuff() |

### Lưu ý quan trọng:

1. **Mỗi Equip là unique instance** - Không share giữa users
2. **State management** - Luôn check state trước khi thao tác
3. **Recalculate stats** - Sau mỗi thay đổi equip/level/star
4. **Validation** - Check maxLevel, maxStar, ownership
5. **Slot conflict** - Auto unequip hoặc throw error

---

**BẠN MUỐN TÔI IMPLEMENT PHẦN NÀO TRƯỚC?**
- CRUD cơ bản (add, remove, equip, unequip)?
- Stats calculator?
- Controller & API endpoints?
- Frontend integration?

