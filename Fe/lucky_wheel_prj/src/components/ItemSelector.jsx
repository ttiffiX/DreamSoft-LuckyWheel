import { useState, useEffect } from 'react';
import './ItemSelector.css';

const API_BASE_URL = 'http://localhost:8080';

// Item mapping (match với items.json trong backend)
const ITEM_INFO = {
  1: { name: 'Gold', itemType: 1, canTrade: 1 },
  2: { name: 'Diamond', itemType: 2, canTrade: 1 },
  3: { name: 'Normal Ticket', itemType: 3, canTrade: 0 },
  4: { name: 'Premium Ticket', itemType: 4, canTrade: 0 }
};

function ItemSelector({ username, onClose, onAddItem }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedItem, setSelectedItem] = useState(null);
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    fetchData();
  }, [username]);

  const fetchData = async () => {
    try {
      // Fetch user data with resources
      const userResponse = await fetch(`${API_BASE_URL}/users/${username}`);
      const userData = await userResponse.json();
      setUser(userData);

      setLoading(false);
    } catch (err) {
      console.error('Failed to fetch data:', err);
      setLoading(false);
    }
  };

  const handleSelectItem = (item) => {
    setSelectedItem(item);
    setQuantity(1);
  };

  const handleAddItem = () => {
    if (selectedItem && quantity > 0) {
      onAddItem(selectedItem.id, quantity);
      onClose();
    }
  };

  const getAvailableQuantity = (itemId) => {
    if (!user || !user.resources) return 0;
    return user.resources[itemId] || 0;
  };

  // Build tradable items list from user's resources
  const buildTradableItems = () => {
    if (!user || !user.resources) return [];

    const items = [];

    // Loop through user's resources
    for (const [itemId, qty] of Object.entries(user.resources)) {
      const itemIdNum = parseInt(itemId);
      const itemInfo = ITEM_INFO[itemIdNum];

      // Only show items that:
      // 1. Have quantity > 0
      // 2. Can be traded (canTrade = 1)
      // 3. Exist in our item mapping
      if (itemInfo && itemInfo.canTrade === 1 && qty > 0) {
        items.push({
          id: itemIdNum,
          name: itemInfo.name,
          itemType: itemInfo.itemType,
          quantity: qty
        });
      }
    }

    return items;
  };

  const tradableItems = buildTradableItems();

  if (loading) return <div className="modal-overlay"><div className="loading">Loading...</div></div>;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="item-selector-modal" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Select Item to Trade</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          <div className="items-grid">
            {tradableItems.length === 0 ? (
              <p className="no-items">No tradable items available</p>
            ) : (
              tradableItems.map(item => {
                const available = getAvailableQuantity(item.id);
                const isSelected = selectedItem?.id === item.id;

                return (
                  <div
                    key={item.id}
                    className={`item-card ${isSelected ? 'selected' : ''}`}
                    onClick={() => handleSelectItem(item)}
                  >
                    <div className="item-icon">📦</div>
                    <div className="item-name">{item.name}</div>
                    <div className="item-type">{item.itemType}</div>
                    <div className="item-quantity">Available: {available}</div>
                  </div>
                );
              })
            )}
          </div>

          {selectedItem && (
            <div className="quantity-selector">
              <h3>Selected: {selectedItem.name}</h3>
              <div className="quantity-controls">
                <label>Quantity:</label>
                <input
                  type="number"
                  min="1"
                  max={getAvailableQuantity(selectedItem.id)}
                  value={quantity}
                  onChange={(e) => setQuantity(Math.max(1, Math.min(getAvailableQuantity(selectedItem.id), parseInt(e.target.value) || 1)))}
                />
                <span>/ {getAvailableQuantity(selectedItem.id)}</span>
              </div>
              <button className="add-item-btn" onClick={handleAddItem}>
                Add to Trade
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ItemSelector;

