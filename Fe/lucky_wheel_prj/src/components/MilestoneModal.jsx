import { useState, useEffect } from 'react';

const API_BASE_URL = 'http://localhost:8080';

function MilestoneModal({ wheelId, userId, onClose, onResourceUpdate }) {
  const [milestones, setMilestones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [claiming, setClaiming] = useState(null);

  useEffect(() => {
    fetchAvailableMilestones();
  }, [wheelId]);

  const fetchAvailableMilestones = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `${API_BASE_URL}/milestones?userId=${userId}&wheelId=${wheelId}`
      );

      if (!response.ok) {
        throw new Error('Failed to fetch milestones');
      }

      const data = await response.json();
      setMilestones(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleClaimMilestone = async (milestoneId) => {
    setClaiming(milestoneId);
    setError(null);

    try {
      const response = await fetch(
        `${API_BASE_URL}/milestones?userId=${userId}&wheelId=${wheelId}&milestoneId=${milestoneId}`,
        {
          method: 'POST',
        }
      );

      if (!response.ok) {
        throw new Error('Failed to claim milestone');
      }

      const claimedRewards = await response.json();

      // Show success message
      alert(
        `✅ Claimed rewards:\n${claimedRewards
          .map((r) => `${r.number}x ${r.itemName}`)
          .join('\n')}`
      );

      // Refresh milestones list
      fetchAvailableMilestones();

      // Refresh user resources
      if (onResourceUpdate) {
        onResourceUpdate();
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setClaiming(null);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content milestone-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>🏆 Milestone Rewards</h2>
          <button className="close-btn" onClick={onClose}>
            ×
          </button>
        </div>

        {loading && <div className="loading">Loading milestones...</div>}

        {error && (
          <div className="error">
            ❌ {error}
          </div>
        )}

        {!loading && !error && (
          <>
            {milestones.length === 0 ? (
              <div className="empty-state">
                <p>🎯 No milestones available to claim yet.</p>
                <p>Keep spinning to reach the next milestone!</p>
              </div>
            ) : (
              <div className="milestones-list">
                {milestones.map((milestone) => (
                  <div key={milestone.id} className="milestone-card">
                    <div className="milestone-header">
                      <h3>🎊 Milestone {milestone.milestone}</h3>
                      <span className="milestone-badge">Ready to Claim!</span>
                    </div>
                    <div className="milestone-rewards">
                      <h4>Rewards:</h4>
                      <ul>
                        {milestone.rewards.map((reward, idx) => (
                          <li key={idx}>
                            {reward.number}x {reward.itemName}
                          </li>
                        ))}
                      </ul>
                    </div>
                    <button
                      className="claim-btn"
                      onClick={() => handleClaimMilestone(milestone.id)}
                      disabled={claiming === milestone.id}
                    >
                      {claiming === milestone.id ? 'Claiming...' : '🎁 Claim Rewards'}
                    </button>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default MilestoneModal;

