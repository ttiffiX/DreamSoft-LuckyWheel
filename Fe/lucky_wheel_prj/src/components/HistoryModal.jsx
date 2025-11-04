import { useState, useEffect } from 'react';

const API_BASE_URL = 'http://localhost:8080';

function HistoryModal({ wheelId, userId, onClose }) {
  const [historyData, setHistoryData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);

  useEffect(() => {
    fetchHistory(currentPage);
  }, [wheelId, currentPage]);

  const fetchHistory = async (page) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `${API_BASE_URL}/reward-history?userId=${userId}&wheelId=${wheelId}&page=${page}&size=${pageSize}`
      );

      if (!response.ok) {
        throw new Error('Failed to fetch history');
      }

      const data = await response.json();
      setHistoryData(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handlePreviousPage = () => {
    if (currentPage > 0) {
      setCurrentPage(currentPage - 1);
    }
  };

  const handleNextPage = () => {
    if (historyData && currentPage < historyData.totalPages - 1) {
      setCurrentPage(currentPage + 1);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>🎲 Spin History</h2>
          <button className="close-btn" onClick={onClose}>
            ×
          </button>
        </div>

        {loading && <div className="loading">Loading...</div>}

        {error && (
          <div className="error">
            ❌ {error}
          </div>
        )}

        {!loading && !error && historyData && (
          <>
            <table className="history-table">
              <thead>
                <tr>
                  <th>Reward</th>
                  <th>Type</th>
                  <th>Ticket Type</th>
                  <th>Quantity</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {historyData.content.map((item, index) => (
                  <tr key={index}>
                    <td>{item.rewardName}</td>
                    <td>{item.rewardType}</td>
                    <td>{item.ticketType}</td>
                    <td>x{item.quantity}</td>
                    <td>{formatDate(item.spinTime)}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {historyData.content.length === 0 && (
              <div className="loading">No history found</div>
            )}

            <div className="pagination">
              <button
                onClick={handlePreviousPage}
                disabled={currentPage === 0}
              >
                ← Previous
              </button>
              <span>
                Page {currentPage + 1} of {historyData.totalPages || 1}
              </span>
              <button
                onClick={handleNextPage}
                disabled={currentPage >= historyData.totalPages - 1}
              >
                Next →
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default HistoryModal;
