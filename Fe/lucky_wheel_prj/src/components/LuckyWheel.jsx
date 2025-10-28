import { useState } from 'react';
import SpinResult from './SpinResult';

const API_BASE_URL = 'http://localhost:8080';

function LuckyWheel({ wheelId, onShowHistory }) {
  const [spinning, setSpinning] = useState(false);
  const [spinResult, setSpinResult] = useState(null);
  const [error, setError] = useState(null);

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
          userId: 1, // Temporary user ID, you can make this dynamic later
          wheelId: wheelId,
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
      }, 2000);
    } catch (err) {
      setError(err.message);
      setSpinning(false);
    }
  };

  return (
    <div className="lucky-wheel-container">
      {/*<div className="wheel-display">*/}
      {/*  <div className={`wheel-icon ${spinning ? 'spinning' : ''}`}>*/}
      {/*    🎡*/}
      {/*  </div>*/}
      {/*</div>*/}

      <div className="spin-buttons">
        <button
          className="spin-btn"
          onClick={() => handleSpin(1)}
          disabled={spinning}
        >
          {spinning ? 'Spinning...' : 'Spin x1'}
        </button>
        <button
          className="spin-btn"
          onClick={() => handleSpin(10)}
          disabled={spinning}
        >
          {spinning ? 'Spinning...' : 'Spin x10'}
        </button>
      </div>

      {error && (
        <div className="error">
          ❌ {error}
        </div>
      )}

      {spinResult && <SpinResult results={spinResult} />}

      <button className="history-btn" onClick={onShowHistory}>
        📜 View History
      </button>
    </div>
  );
}

export default LuckyWheel;

