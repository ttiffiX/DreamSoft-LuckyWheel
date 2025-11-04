# User Resources Display

## Hiển thị Resources của User

### Vị trí hiển thị:
- Nằm ở **User Info Bar** (thanh trên cùng)
- Bên cạnh username
- Hiển thị tất cả resources từ User entity

### Format hiển thị:
```
👤 username | 🪙 Gold: 10,000 | 💎 Diamond: 500 | 🎫 Normal Ticket: 3 | 🎟️ Premium Ticket: 1 | [Logout]
```

### Cấu trúc dữ liệu từ BE:
```json
{
  "id": "user123",
  "username": "player1",
  "resources": {
    "GOLD": 10000,
    "DIAMOND": 500,
    "NORMAL_TICKET": 3,
    "PREMIUM_TICKET": 1
  }
}
```

### Component UserResources:
- Tự động fetch data từ `/users/{username}`
- Lấy field `resources` từ User entity
- Hiển thị với icon và format số đẹp
- Auto refresh khi username thay đổi

### Icon mapping:
- GOLD → 🪙
- DIAMOND → 💎
- NORMAL_TICKET → 🎫
- PREMIUM_TICKET → 🎟️

### Responsive:
- Tự động xuống dòng khi màn hình nhỏ
- Hover effect cho từng resource item
- Loading state khi fetch data

