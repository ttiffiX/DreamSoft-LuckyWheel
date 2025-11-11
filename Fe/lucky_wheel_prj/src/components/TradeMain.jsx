import { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import UserList from './UserList';
import TradeRoom from './TradeRoom';
import './TradeMain.css';

const API_BASE_URL = 'http://localhost:8080';
const WS_URL = 'http://localhost:8080/ws';

function TradeMain({ currentUser, onBack, onResourceUpdate }) {
  const [selectedPartner, setSelectedPartner] = useState(null);
  const [activeTradeId, setActiveTradeId] = useState(null);
  const [myTrades, setMyTrades] = useState([]);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState('');
  const [wsConnected, setWsConnected] = useState(false);
  const stompClientRef = useRef(null);

  useEffect(() => {
    fetchMyTrades();
    connectWebSocket();

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, []);

  const connectWebSocket = () => {
    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        setWsConnected(true);

        const subscriptionPath = `/topic/user-${currentUser.id}/trade-updates`;
        client.subscribe(subscriptionPath, (message) => {
          const data = JSON.parse(message.body);
          handleTradeNotification(data);
        });

        fetchMyTrades();
      },
      onDisconnect: () => {
        setWsConnected(false);
      },
      onStompError: (frame) => {
        console.error('WebSocket error:', frame);
        setWsConnected(false);
      },
      onWebSocketError: (error) => {
        console.error('WebSocket error:', error);
        setWsConnected(false);
      }
    });

    client.activate();
    stompClientRef.current = client;
  };

  const handleTradeNotification = (data) => {
    switch (data.eventType) {
      case 'TRADE_CREATED':
        showNotification(`${data.trade.initUserId} wants to trade with you!`);
        break;
      case 'TRADE_ACCEPTED':
        showNotification('Trade request accepted!');
        break;
      case 'ITEM_ADDED':
        showNotification('Partner added items');
        break;
      case 'ITEM_REMOVED':
        showNotification('Partner removed items');
        break;
      case 'TRADE_CONFIRMED':
        showNotification('Partner is ready!');
        break;
      case 'TRADE_COMPLETED':
        showNotification('Trade completed! 🎉');
        break;
      case 'TRADE_CANCELLED':
        showNotification('Trade was cancelled');
        break;
      default:
        break;
    }

    fetchMyTrades();
  };

  const showNotification = (message) => {
    setNotification(message);
    setTimeout(() => setNotification(''), 5000);
  };

  const fetchMyTrades = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/trades?userId=${currentUser.id}`);
      if (!response.ok) return;

      const trades = await response.json();

      const pending = trades.filter(t =>
        t.partnerUserId === currentUser.id && t.status === 'PENDING'
      );

      const active = trades.filter(t =>
        t.status === 'ACTIVE' || (t.status === 'PENDING' && t.initUserId === currentUser.id)
      );

      setPendingRequests(pending);
      setMyTrades(active);
    } catch (err) {
      console.error('Error fetching trades:', err);
    }
  };

  const handleSelectUser = async (partnerUser) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/trades`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          initUserId: currentUser.id,
          partnerUserId: partnerUser.id
        })
      });

      if (!response.ok) throw new Error('Failed to create trade');

      const trade = await response.json();
      await fetchMyTrades();

      showNotification('Trade request sent!');
      setLoading(false);

      setActiveTradeId(trade.id);
      setSelectedPartner(partnerUser);
    } catch (err) {
      console.error('Error creating trade:', err);
      alert('Error creating trade: ' + err.message);
      setLoading(false);
    }
  };

  const handleAcceptTradeRequest = async (trade) => {
    try {
      const response = await fetch(`${API_BASE_URL}/trades/${trade.id}?partnerUserId=${currentUser.id}`, {
        method: 'POST'
      });

      if (!response.ok) throw new Error('Failed to accept trade');

      const updatedTrade = await response.json();

      const partnerResponse = await fetch(`${API_BASE_URL}/users`);
      const allUsers = await partnerResponse.json();
      const partner = allUsers.find(u => u.id === trade.initUserId);

      await fetchMyTrades();

      showNotification('Trade accepted!');

      setActiveTradeId(updatedTrade.id);
      setSelectedPartner(partner);
    } catch (err) {
      console.error('Error accepting trade:', err);
      alert('Error accepting trade: ' + err.message);
    }
  };

  const handleOpenTrade = async (trade) => {
    try {
      // Find partner
      const partnerId = trade.initUserId === currentUser.id ? trade.partnerUserId : trade.initUserId;
      const response = await fetch(`${API_BASE_URL}/users`);
      const allUsers = await response.json();
      const partner = allUsers.find(u => u.id === partnerId);

      setActiveTradeId(trade.id);
      setSelectedPartner(partner);
    } catch (err) {
      alert('Error opening trade: ' + err.message);
    }
  };

  const handleCancelTrade = async (tradeId) => {
    if (!confirm('Cancel this trade?')) return;

    try {
      await fetch(`${API_BASE_URL}/trades/${tradeId}/cancel?userId=${currentUser.id}`, {
        method: 'POST'
      });

      await fetchMyTrades();
      showNotification('Trade cancelled');
    } catch (err) {
      console.error('Error cancelling trade:', err);
      alert('Error cancelling trade: ' + err.message);
    }
  };

  const handleBackToUserList = () => {
    setSelectedPartner(null);
    setActiveTradeId(null);
    fetchMyTrades();
  };

  if (loading) {
    return <div className="loading">Creating trade...</div>;
  }

  if (activeTradeId && selectedPartner) {
    return (
      <TradeRoom
        tradeId={activeTradeId}
        currentUser={currentUser}
        partnerUser={selectedPartner}
        onBack={handleBackToUserList}
        onResourceUpdate={onResourceUpdate}
      />
    );
  }

  return (
    <div className="trade-main">
      {notification && (
        <div className="trade-notification">{notification}</div>
      )}

      <div className="trade-main-header">
        <button className="back-btn" onClick={onBack}>← Back to Wheels</button>
        <h1>🤝 Trade Center</h1>
        <div className="ws-status">
          {wsConnected ? (
            <span className="ws-connected">🟢 Live</span>
          ) : (
            <span className="ws-disconnected">🔴 Connecting...</span>
          )}
        </div>
      </div>

      {/* Pending Trade Requests */}
      {pendingRequests.length > 0 && (
        <div className="pending-trades-section">
          <h2>📬 Incoming Trade Requests ({pendingRequests.length})</h2>
          <div className="trades-list">
            {pendingRequests.map(trade => (
              <div key={trade.id} className="trade-card pending">
                <div className="trade-info">
                  <div className="trade-partner">
                    <span className="partner-icon">👤</span>
                    <span className="partner-name">{trade.initUserId}</span>
                  </div>
                  <div className="trade-status-text">wants to trade with you</div>
                  <div className="trade-time">{new Date(trade.createdAt).toLocaleString()}</div>
                </div>
                <div className="trade-actions-inline">
                  <button
                    className="accept-request-btn"
                    onClick={() => handleAcceptTradeRequest(trade)}
                  >
                    ✓ Accept
                  </button>
                  <button
                    className="cancel-trade-btn"
                    onClick={() => handleCancelTrade(trade.id)}
                  >
                    ✕ Decline
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* My Active Trades */}
      {myTrades.length > 0 && (
        <div className="my-trades-section">
          <h2>📋 My Trades ({myTrades.length})</h2>
          <div className="trades-list">
            {myTrades.map(trade => {
              const partnerId = trade.initUserId === currentUser.id ? trade.partnerUserId : trade.initUserId;
              const isPending = trade.status === 'PENDING';

              return (
                <div key={trade.id} className={`trade-card ${trade.status.toLowerCase()}`}>
                  <div className="trade-info">
                    <div className="trade-partner">
                      <span className="partner-icon">👤</span>
                      <span className="partner-name">{partnerId}</span>
                    </div>
                    <div className="trade-status-badge">
                      <span className={`status-dot ${trade.status.toLowerCase()}`}></span>
                      {trade.status}
                      {isPending && ' (Waiting for partner)'}
                    </div>
                    <div className="trade-time">{new Date(trade.createdAt).toLocaleString()}</div>
                  </div>
                  <div className="trade-actions-inline">
                    <button
                      className="open-trade-btn"
                      onClick={() => handleOpenTrade(trade)}
                    >
                      Open
                    </button>
                    <button
                      className="cancel-trade-btn"
                      onClick={() => handleCancelTrade(trade.id)}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Create New Trade */}
      <div className="create-trade-section">
        <h2>👥 Start New Trade</h2>
        <UserList
          currentUserId={currentUser.id}
          onSelectUser={handleSelectUser}
        />
      </div>
    </div>
  );
}

export default TradeMain;

