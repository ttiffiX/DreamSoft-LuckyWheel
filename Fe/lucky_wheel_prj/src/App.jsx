import { useState } from 'react';
import './App.css';
import LuckyWheel from './components/LuckyWheel';
import HistoryModal from './components/HistoryModal';

function App() {
  const [selectedWheelId, setSelectedWheelId] = useState(1);
  const [showHistory, setShowHistory] = useState(false);

  const wheels = [
    { id: 1, name: 'Normal Wheel' },
    { id: 2, name: 'Premium Wheel' }
  ];

  return (
    <div className="app">
      <div className="app-header">
        <h1>🎡 Lucky Wheel</h1>
        <div className="wheel-selector">
          {wheels.map(wheel => (
            <button
              key={wheel.id}
              className={`wheel-btn ${selectedWheelId === wheel.id ? 'active' : ''}`}
              onClick={() => setSelectedWheelId(wheel.id)}
            >
              {wheel.name}
            </button>
          ))}
        </div>
      </div>

      <LuckyWheel
        wheelId={selectedWheelId}
        onShowHistory={() => setShowHistory(true)}
      />

      {showHistory && (
        <HistoryModal
          wheelId={selectedWheelId}
          onClose={() => setShowHistory(false)}
        />
      )}
    </div>
  );
}

export default App;

