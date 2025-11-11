import { useState, useEffect } from 'react';
import './UserList.css';

const API_BASE_URL = 'http://localhost:8080';

function UserList({ currentUserId, onSelectUser }) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (currentUserId) {
      fetchUsers();
    }
  }, [currentUserId]);

  const fetchUsers = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/users`);
      if (!response.ok) throw new Error('Failed to fetch users');

      const data = await response.json();
      const filteredUsers = data.filter(user => user.id !== currentUserId);

      setUsers(filteredUsers);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching users:', err);
      setError(err.message);
      setLoading(false);
    }
  };

  if (loading) return <div className="loading">Loading users...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="user-list-container">
      <div className="user-grid">
        {users.map(user => (
          <div
            key={user.id}
            className="user-card"
            onClick={() => onSelectUser(user)}
          >
            <div className="user-avatar">👤</div>
            <div className="user-name">{user.username}</div>
            <button className="trade-btn">Start Trade</button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default UserList;

