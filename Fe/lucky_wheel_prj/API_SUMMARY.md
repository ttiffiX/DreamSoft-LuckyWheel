# API Endpoints Summary

## Backend Base URL
```
http://localhost:8080
```

## API Endpoints

### User APIs
- **GET /users/{username}** - Get user by username (for login)
  - Response: User object with `id`, `username`, `resources`

### Wheel APIs
- **GET /wheels** - Get all wheels
  - Response: Array of WheelInfoResponse
  
- **GET /wheels/{wheelId}** - Get wheel by ID
  - Response: WheelInfoResponse with gifts and milestones

### Reward History APIs
- **GET /reward-history?userId={userId}&wheelId={wheelId}&page={page}&size={size}** - Get spin history
  - Response: Page<SpinResultResponse>
  
- **POST /reward-history** - Spin wheel
  - Body: `{ userId, wheelId, quantity }`
  - Response: List<SpinResultResponse>

### Milestone APIs
- **GET /milestones?userId={userId}&wheelId={wheelId}** - Get available milestones
  - Response: List<MilestoneInfo>
  
- **POST /milestones?userId={userId}&wheelId={wheelId}&milestoneId={milestoneId}** - Claim milestone
  - Response: List<RewardInfo>

## Frontend Flow

1. **Login Screen** (`/users/{username}`)
   - User enters username
   - Press Enter or click Login
   - System fetches user data
   - Save userId to localStorage

2. **Wheel List** (`/wheels`)
   - Display all available wheels
   - Click wheel to view details

3. **Wheel Detail** (`/wheels/{wheelId}`)
   - Show wheel information
   - Spin x1 or x10 (`POST /reward-history`)
   - View History (`GET /reward-history`)
   - Claim Milestones (`GET /milestones` → `POST /milestones`)

4. **Logout**
   - Clear localStorage
   - Return to login screen

