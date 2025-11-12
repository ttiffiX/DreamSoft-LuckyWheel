import { useState, useEffect } from 'react';
import './ItemSelector.css';

const API_BASE_URL = 'http://localhost:8080';

function ItemSelector({ username, onClose, onAddItem }) {
  const [user, setUser] = useState(null);
  const [userItems, setUserItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedItem, setSelectedItem] = useState(null);
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    fetchData();
  }, [username]);

  const fetchData = async () => {
    try {
      // Fetch user data to get user ID
      const userResponse = await fetch(`${API_BASE_URL}/users/${username}`);
      const userData = await userResponse.json();
      setUser(userData);

      // Fetch user's items with quantities using new API
      const itemsResponse = await fetch(`${API_BASE_URL}/items/user/${userData.id}`);
      const itemsData = await itemsResponse.json();
      setUserItems(itemsData);

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
      onAddItem(selectedItem.itemId, quantity);
      onClose();
    }
  };

  // Filter only tradable items
  const tradableItems = userItems.filter(item => item.canTrade === 1 && item.quantity > 0);

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
                const isSelected = selectedItem?.itemId === item.itemId;

                return (
                  <div
                    key={item.itemId}
                    className={`item-card ${isSelected ? 'selected' : ''}`}
                    onClick={() => handleSelectItem(item)}
                  >
                    <div className="item-icon">📦</div>
                    <div className="item-name">{item.name}</div>
                    <div className="item-type">{item.itemType}</div>
                    <div className="item-quantity">Available: {item.quantity}</div>
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
                  max={selectedItem.quantity}
                  value={quantity}
                  onChange={(e) => setQuantity(Math.max(1, Math.min(selectedItem.quantity, parseInt(e.target.value) || 1)))}
                />
                <span>/ {selectedItem.quantity}</span>
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
