# User Resources Auto-Refresh Feature

## Vấn đề
Sau khi user spin wheel hoặc claim milestone, resources đã thay đổi trên server nhưng thanh user-info-bar không được cập nhật.

## Giải pháp

### 1. **UserResources.jsx**
- Thêm prop `refreshTrigger`
- Sử dụng `useCallback` để tạo stable `fetchUserResources` function
- `useEffect` sẽ re-run khi `refreshTrigger` thay đổi

```javascript
const fetchUserResources = useCallback(async () => {
  // Fetch user data from API
}, [username]);

useEffect(() => {
  fetchUserResources();
}, [fetchUserResources, refreshTrigger]);
```

### 2. **App.jsx**
- Thêm state `resourceRefreshTrigger`
- Tạo callback `handleResourceUpdate()` để increment trigger
- Truyền `refreshTrigger` xuống `UserResources`
- Truyền `onResourceUpdate` xuống `WheelDetail` và `MilestoneModal`

```javascript
const [resourceRefreshTrigger, setResourceRefreshTrigger] = useState(0);

const handleResourceUpdate = () => {
  setResourceRefreshTrigger(prev => prev + 1);
};
```

### 3. **WheelDetail.jsx**
- Nhận prop `onResourceUpdate`
- Gọi callback sau khi spin thành công

```javascript
setTimeout(() => {
  setSpinResult(data);
  setSpinning(false);
  if (onResourceUpdate) {
    onResourceUpdate(); // ← Refresh resources
  }
}, 2000);
```

### 4. **MilestoneModal.jsx**
- Nhận prop `onResourceUpdate`
- Gọi callback sau khi claim thành công

```javascript
fetchAvailableMilestones();
if (onResourceUpdate) {
  onResourceUpdate(); // ← Refresh resources
}
```

## Flow hoạt động

```
User spin wheel → WheelDetail calls onResourceUpdate()
                ↓
          App.jsx increments resourceRefreshTrigger
                ↓
          UserResources detects trigger change
                ↓
          Fetch fresh user data from API
                ↓
          Update displayed resources ✅
```

## Kết quả
✅ Resources tự động cập nhật sau khi spin
✅ Resources tự động cập nhật sau khi claim milestone
✅ Không cần reload page
✅ Real-time update

## Technical Details
- Sử dụng counter pattern để trigger re-fetch
- Mỗi lần increment counter → useEffect re-run
- Callback chain: Child → Parent → Sibling
- No prop drilling vì chỉ 1-2 level

