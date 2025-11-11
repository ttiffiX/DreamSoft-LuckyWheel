import { useState, useEffect } from 'react';
import './App.css';
import Login from './components/Login';
import WheelList from './components/WheelList';
import WheelDetail from './components/WheelDetail';
import HistoryModal from './components/HistoryModal';
import MilestoneModal from './components/MilestoneModal';
import UserResources from './components/UserResources';
import TradeMain from './components/TradeMain';

function App() {
  const [user, setUser] = useState(null);
  const [selectedWheel, setSelectedWheel] = useState(null);
  const [showHistory, setShowHistory] = useState(false);
  const [showMilestone, setShowMilestone] = useState(false);
  const [historyWheelId, setHistoryWheelId] = useState(null);
  const [milestoneWheelId, setMilestoneWheelId] = useState(null);
  const [resourceRefreshTrigger, setResourceRefreshTrigger] = useState(0);
  const [showTrade, setShowTrade] = useState(false);

  // Check if user is already logged in
  useEffect(() => {
    const savedUserId = localStorage.getItem('userId');
    const savedUsername = localStorage.getItem('username');
    const savedUserData = localStorage.getItem('userData');

    if (savedUserId && savedUsername && savedUserData) {
      try {
        const userData = JSON.parse(savedUserData);
        setUser(userData);
      } catch (err) {
        console.error('Failed to parse saved user data:', err);
        localStorage.clear();
      }
    }
  }, []);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.clear();
    setUser(null);
    setSelectedWheel(null);
  };

  const handleSelectWheel = (wheel) => {
    setSelectedWheel(wheel);
  };

  const handleBackToList = () => {
    setSelectedWheel(null);
  };

  const handleShowHistory = (wheelId) => {
    setHistoryWheelId(wheelId);
    setShowHistory(true);
  };

  const handleShowMilestone = (wheelId) => {
    setMilestoneWheelId(wheelId);
    setShowMilestone(true);
  };

  const handleResourceUpdate = () => {
    // Trigger resource refresh by incrementing counter
    setResourceRefreshTrigger(prev => prev + 1);
  };

  // If not logged in, show login screen
  if (!user) {
    return (
      <div className="app">
        <Login onLoginSuccess={handleLoginSuccess} />
      </div>
    );
  }

  return (
    <div className="app">
      <div className="user-info-bar">
        <div className="user-info-left">
          <span className="username-display">👤 {user.username}</span>
          <UserResources username={user.username} refreshTrigger={resourceRefreshTrigger} />
        </div>
        <div className="nav-select-wrapper">
          <select
            className="nav-select"
            value={showTrade ? 'trade' : 'wheels'}
            onChange={(e) => {
              const newValue = e.target.value === 'trade';
              setShowTrade(newValue);
              setSelectedWheel(null);
            }}
          >
            <option value="wheels">🎡 Lucky Wheels</option>
            <option value="trade">🤝 Trade Center</option>
          </select>
        </div>
        <button className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </div>

      {showTrade ? (
        <TradeMain
          currentUser={user}
          onBack={() => setShowTrade(false)}
          onResourceUpdate={handleResourceUpdate}
        />
      ) : (
        <>
          {!selectedWheel ? (
            <WheelList onSelectWheel={handleSelectWheel} />
          ) : (
            <WheelDetail
              wheel={selectedWheel}
              userId={user.id}
              onBack={handleBackToList}
              onShowHistory={handleShowHistory}
              onShowMilestone={handleShowMilestone}
              onResourceUpdate={handleResourceUpdate}
            />
          )}
        </>
      )}

      {showHistory && historyWheelId && (
        <HistoryModal
          wheelId={historyWheelId}
          userId={user.id}
          onClose={() => setShowHistory(false)}
        />
      )}

      {showMilestone && milestoneWheelId && (
        <MilestoneModal
          wheelId={milestoneWheelId}
          userId={user.id}
          onClose={() => setShowMilestone(false)}
          onResourceUpdate={handleResourceUpdate}
        />
      )}
    </div>
  );
}

export default App;

