import { useState, useEffect } from 'react';
import SpinResult from './SpinResult';

const API_BASE_URL = 'http://localhost:8080';

function WheelDetail({ wheel, userId, onBack, onShowHistory, onShowMilestone, onResourceUpdate }) {
  const [spinning, setSpinning] = useState(false);
  const [spinResult, setSpinResult] = useState(null);
  const [error, setError] = useState(null);
  const [wheelData, setWheelData] = useState(wheel);

  useEffect(() => {
    // Fetch fresh wheel data with milestones
    fetchWheelData();
  }, [wheel.wheelId]);

  const fetchWheelData = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/wheels/${wheel.wheelId}`);
      if (!response.ok) {
        throw new Error('Failed to fetch wheel data');
      }
      const data = await response.json();
      setWheelData(data);
    } catch (err) {
      console.error('Failed to fetch wheel details:', err);
    }
  };

  const handleSpin = async (quantity) => {
    setSpinning(true);
    setError(null);
    setSpinResult(null);

    try {
      const response = await fetch(`${API_BASE_URL}/reward-history`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: userId,
          wheelId: wheel.wheelId,
          quantity: quantity,
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to spin the wheel');
      }

      const data = await response.json();

      // Simulate spinning animation
      setTimeout(() => {
        setSpinResult(data);
        setSpinning(false);
        // Refresh user resources after successful spin
        if (onResourceUpdate) {
          onResourceUpdate();
        }
      }, 2000);
    } catch (err) {
      setError(err.message);
      setSpinning(false);
    }
  };

  return (
    <div className="wheel-detail-container">
      <div className="wheel-detail-header">
        <button className="back-btn" onClick={onBack}>
          ← Back to Wheels
        </button>
        <h1>🎰 {wheelData.wheelName}</h1>
      </div>

      <div className="wheel-info-card">
        <div className="info-section">
          <h3>Requirements: {wheelData.resourceType} ticket</h3>
        </div>

        <div className="info-section">
          <h3>🎁 Possible Rewards</h3>
          <div className="rewards-grid">
            {wheelData.gifts?.map((gift) => (
              <div key={gift.id} className="reward-item">
                <span className="reward-name">{gift.itemName}</span>
                <span className="reward-amount">x{gift.number}</span>
                <span className="reward-chance">{gift.probability}%</span>
              </div>
            ))}
          </div>
        </div>

        <div className="info-section">
          <h3>🏆 Milestones</h3>
          <div className="milestones-preview">
            {wheelData.milestones?.map((milestone) => (
              <div key={milestone.id} className="milestone-item">
                <span className="milestone-count">Spin {milestone.milestone}x</span>
                <span className="milestone-rewards">
                  {milestone.rewards.map(r => `${r.number}x ${r.itemName}`).join(', ')}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="action-buttons">
        <button
          className="spin-btn primary"
          onClick={() => handleSpin(1)}
          disabled={spinning}
        >
          {spinning ? '🎲 Spinning...' : '🎲 Spin x1'}
        </button>
        <button
          className="spin-btn primary"
          onClick={() => handleSpin(10)}
          disabled={spinning}
        >
          {spinning ? '🎲 Spinning...' : '🎲 Spin x10'}
        </button>
      </div>

      <div className="secondary-buttons">
        <button className="btn-secondary" onClick={() => onShowMilestone(wheel.wheelId)}>
          🏆 Claim Milestone Rewards
        </button>
        <button className="btn-secondary" onClick={() => onShowHistory(wheel.wheelId)}>
          📜 View Spin History
        </button>
      </div>

      {error && (
        <div className="error">
          ❌ {error}
        </div>
      )}

      {spinResult && <SpinResult results={spinResult} />}
    </div>
  );
}

export default WheelDetail;

