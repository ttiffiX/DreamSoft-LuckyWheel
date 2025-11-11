import { useState, useEffect, useCallback } from 'react';

const API_BASE_URL = 'http://localhost:8080';

function UserResources({ username, refreshTrigger }) {
  const [resources, setResources] = useState({});
  const [loading, setLoading] = useState(false);

  const fetchUserResources = useCallback(async () => {
    if (!username) return;

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/users/${username}`);
      if (response.ok) {
        const userData = await response.json();
        setResources(userData.resources || {});
      }
    } catch (err) {
      console.error('Failed to fetch user resources:', err);
    } finally {
      setLoading(false);
    }
  }, [username]);

  useEffect(() => {
    fetchUserResources();
  }, [fetchUserResources, refreshTrigger]);

  const getResourceIcon = (resourceType) => {
    const icons = {
      1: '🪙',
      2: '💎',
      3: '🎫',
      4: '🎟️'
    };
    return icons[resourceType] || '📦';
  };

  const getResourceName = (resourceType) => {
    const names = {
      1: 'Gold',
      2: 'Diamond',
      3: 'Normal Ticket',
      4: 'Premium Ticket'
    };
    return names[resourceType] || resourceType;
  };

  if (loading) {
    return <div className="user-resources loading">Loading resources...</div>;
  }

  return (
    <div className="user-resources">
      {Object.entries(resources).map(([resourceType, amount]) => (
        <div key={resourceType} className="resource-item">
          <span className="resource-icon">{getResourceIcon(resourceType)}</span>
          <span className="resource-name">{getResourceName(resourceType)}</span>
          <span className="resource-amount">{amount.toLocaleString()}</span>
        </div>
      ))}
      {Object.keys(resources).length === 0 && (
        <div className="no-resources">No resources available</div>
      )}
    </div>
  );
}

export default UserResources;

