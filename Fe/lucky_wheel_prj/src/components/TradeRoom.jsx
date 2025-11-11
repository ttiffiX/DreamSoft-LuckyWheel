import { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import ItemSelector from './ItemSelector';
import './TradeRoom.css';

const API_BASE_URL = 'http://localhost:8080';
const WS_URL = 'http://localhost:8080/ws';

function TradeRoom({ tradeId, currentUser, partnerUser, onBack, onResourceUpdate }) {
  const [trade, setTrade] = useState(null);
  const [showItemSelector, setShowItemSelector] = useState(false);
  const [notification, setNotification] = useState('');
  const [loading, setLoading] = useState(true);
  const stompClientRef = useRef(null);

  useEffect(() => {
    fetchTradeData();
    connectWebSocket();

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, [tradeId]);

  const connectWebSocket = () => {
    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 5000,
      onConnect: () => {
        const subscriptionPath = `/topic/user-${currentUser.id}/trade-updates`;
        client.subscribe(subscriptionPath, (message) => {
          const data = JSON.parse(message.body);
          handleWebSocketMessage(data);
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      }
    });

    client.activate();
    stompClientRef.current = client;
  };

  const handleWebSocketMessage = (data) => {
    switch (data.eventType) {
      case 'ITEM_ADDED':
        showNotification('Item added to trade');
        setTrade(data.trade);
        break;
      case 'ITEM_REMOVED':
        showNotification('Item removed from trade');
        setTrade(data.trade);
        break;
      case 'TRADE_CONFIRMED':
        showNotification('Partner is ready!');
        setTrade(data.trade);
        break;
      case 'TRADE_COMPLETED':
        showNotification('Trade completed successfully! 🎉');
        setTrade(data.trade);

        if (onResourceUpdate) {
          onResourceUpdate();
        }

        setTimeout(() => onBack(), 3000);
        break;
      case 'TRADE_CANCELLED':
        showNotification('Trade was cancelled');
        setTrade(data.trade);
        setTimeout(() => onBack(), 2000);
        break;
      default:
        setTrade(data.trade);
    }
  };

  const showNotification = (message) => {
    setNotification(message);
    setTimeout(() => setNotification(''), 3000);
  };

  const fetchTradeData = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/trades/${tradeId}`);
      if (!response.ok) throw new Error('Failed to fetch trade');
      const data = await response.json();
      setTrade(data);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching trade:', err);
      setLoading(false);
    }
  };

  const handleAddItem = async (itemId, quantity) => {
    try {
      const response = await fetch(`${API_BASE_URL}/trades/${tradeId}/add-item`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.id,
          itemId: itemId,
          quantity: quantity
        })
      });

      if (!response.ok) throw new Error('Failed to add item');
      const updatedTrade = await response.json();
      setTrade(updatedTrade);
      showNotification('Item added!');
    } catch (err) {
      console.error('Error adding item:', err);
      alert('Error adding item: ' + err.message);
    }
  };

  const handleRemoveItem = async (itemId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/trades/${tradeId}/remove-item`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.id,
          itemId: itemId
        })
      });

      if (!response.ok) throw new Error('Failed to remove item');
      const updatedTrade = await response.json();
      setTrade(updatedTrade);
      showNotification('Item removed!');
    } catch (err) {
      console.error('Error removing item:', err);
      alert('Error removing item: ' + err.message);
    }
  };

  const handleAccept = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/trades/${tradeId}/accept?userId=${currentUser.id}`, {
        method: 'POST'
      });

      if (!response.ok) throw new Error('Failed to accept trade');
      const updatedTrade = await response.json();
      setTrade(updatedTrade);
      showNotification('You are ready!');
    } catch (err) {
      console.error('Error accepting trade:', err);
      alert('Error accepting trade: ' + err.message);
    }
  };

  const handleCancel = async () => {
    if (!confirm('Are you sure you want to cancel this trade?')) return;

    try {
      const response = await fetch(`${API_BASE_URL}/trades/${tradeId}/cancel?userId=${currentUser.id}`, {
        method: 'POST'
      });

      if (!response.ok) throw new Error('Failed to cancel trade');
      setTimeout(() => onBack(), 1000);
    } catch (err) {
      console.error('Error cancelling trade:', err);
      alert('Error cancelling trade: ' + err.message);
    }
  };

  if (loading) return <div className="loading">Loading trade...</div>;
  if (!trade) return <div className="error">Trade not found</div>;

  const isCurrentUserInit = trade.initUserId === currentUser.id;
  const currentUserItems = isCurrentUserInit ? trade.initUserItems : trade.partnerUserItems;
  const partnerUserItems = isCurrentUserInit ? trade.partnerUserItems : trade.initUserItems;
  const isCurrentUserReady = isCurrentUserInit ? trade.initAccepted : trade.partnerAccepted;
  const isPartnerReady = isCurrentUserInit ? trade.partnerAccepted : trade.initAccepted;

  const canModify = trade.status === 'ACTIVE' && !isCurrentUserReady;
  const isTradeCompleted = trade.status === 'COMPLETED';
  const isTradeCancelled = trade.status === 'CANCELLED';

  return (
    <div className="trade-room">
      {notification && (
        <div className="notification">{notification}</div>
      )}

      <div className="trade-header">
        <button className="back-btn" onClick={onBack}>← Back</button>
        <h1>Trade Room</h1>
        <div className="trade-status">
          Status: <span className={`status-badge ${trade.status.toLowerCase()}`}>
            {trade.status}
          </span>
        </div>
      </div>

      <div className="trade-container">
        {/* Current User Side */}
        <div className="trade-side current-user">
          <div className="side-header">
            <h2>👤 {currentUser.username} (You)</h2>
            {isCurrentUserReady && <span className="ready-badge">✓ READY</span>}
          </div>

          <div className="items-container">
            {Object.keys(currentUserItems).length === 0 ? (
              <p className="no-items">No items added yet</p>
            ) : (
              Object.entries(currentUserItems).map(([itemId, item]) => (
                <div key={itemId} className="trade-item">
                  <div className="item-info">
                    <span className="item-icon">📦</span>
                    <div>
                      <div className="item-name">{item.itemName}</div>
                      <div className="item-details">
                        {item.itemType} × {item.quantity}
                      </div>
                    </div>
                  </div>
                  {canModify && (
                    <button
                      className="remove-btn"
                      onClick={() => handleRemoveItem(itemId)}
                    >
                      ×
                    </button>
                  )}
                </div>
              ))
            )}
          </div>

          {canModify && (
            <button
              className="add-item-btn"
              onClick={() => setShowItemSelector(true)}
            >
              + Add Item
            </button>
          )}
        </div>

        {/* Middle Section */}
        <div className="trade-middle">
          <div className="trade-arrow">⇄</div>
        </div>

        {/* Partner Side */}
        <div className="trade-side partner-user">
          <div className="side-header">
            <h2>👤 {partnerUser.username}</h2>
            {isPartnerReady && <span className="ready-badge">✓ READY</span>}
          </div>

          <div className="items-container">
            {Object.keys(partnerUserItems).length === 0 ? (
              <p className="no-items">No items added yet</p>
            ) : (
              Object.entries(partnerUserItems).map(([itemId, item]) => (
                <div key={itemId} className="trade-item">
                  <div className="item-info">
                    <span className="item-icon">📦</span>
                    <div>
                      <div className="item-name">{item.itemName}</div>
                      <div className="item-details">
                        {item.itemType} × {item.quantity}
                      </div>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      {trade.status === 'ACTIVE' && !isTradeCompleted && !isTradeCancelled && (
        <div className="trade-actions">
          {!isCurrentUserReady ? (
            <button className="accept-btn" onClick={handleAccept}>
              ✓ Ready to Trade
            </button>
          ) : (
            <div className="waiting-message">
              {isPartnerReady ? 'Both ready! Completing trade...' : 'Waiting for partner...'}
            </div>
          )}
          <button className="cancel-btn" onClick={handleCancel}>
            Cancel Trade
          </button>
        </div>
      )}

      {isTradeCompleted && (
        <div className="trade-complete-message">
          🎉 Trade completed successfully!
        </div>
      )}

      {isTradeCancelled && (
        <div className="trade-cancelled-message">
          Trade was cancelled
        </div>
      )}

      {showItemSelector && (
        <ItemSelector
          username={currentUser.username}
          onClose={() => setShowItemSelector(false)}
          onAddItem={handleAddItem}
        />
      )}
    </div>
  );
}

export default TradeRoom;

