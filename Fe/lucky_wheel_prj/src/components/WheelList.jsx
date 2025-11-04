import { useState, useEffect } from 'react';

const API_BASE_URL = 'http://localhost:8080';

function WheelList({ onSelectWheel }) {
  const [wheels, setWheels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchWheels();
  }, []);

  const fetchWheels = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${API_BASE_URL}/wheels`);

      if (!response.ok) {
        throw new Error('Failed to fetch wheels');
      }

      const data = await response.json();
      setWheels(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="wheel-list-container">
        <div className="loading">Loading wheels...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="wheel-list-container">
        <div className="error">❌ {error}</div>
      </div>
    );
  }
  return (
    <div className="wheel-list-container">
      <h1>🎡 Lucky Wheel</h1>
      <div className="wheels-grid">
        {wheels.map((wheel) => (
          <div
            key={wheel.wheelId}
            className={`wheel-card ${wheel.active ? '' : 'disabled'}`}
            onClick={() => wheel.active && onSelectWheel(wheel)}
          >
            <div className="wheel-icon">🎰</div>
            <h2>{wheel.wheelName}</h2>
            <p className="wheel-resource">
              💳 Requires: {wheel.resourceType}
            </p>
            {!wheel.active && <span className="inactive-badge">Inactive</span>}
          </div>
        ))}
      </div>
    </div>
  );
}

export default WheelList;

