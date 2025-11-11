import { useState } from 'react';

const API_BASE_URL = 'http://localhost:8080';

function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!username.trim()) {
      setError('Please enter a username');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `${API_BASE_URL}/users/${encodeURIComponent(username)}`
      );

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('User not found. Please check your username.');
        }
        throw new Error('Login failed. Please try again.');
      }

      const userData = await response.json();


      // Save user data to localStorage
      localStorage.setItem('userId', userData.id);
      localStorage.setItem('username', userData.username);
      localStorage.setItem('userData', JSON.stringify(userData));

      // Call success callback with user data (keep 'id', not 'userId')
      onLoginSuccess({
        id: userData.id,  // ← Use 'id' to match backend
        username: userData.username,
        resources: userData.resources
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1>🎡 Lucky Wheel</h1>
        <h2>Welcome!</h2>
        <p className="login-subtitle">Enter your username to continue</p>

        <form onSubmit={handleLogin}>
          <div className="input-group">
            <input
              type="text"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
              className="username-input"
              autoFocus
            />
          </div>

          {error && (
            <div className="error-message">
              ❌ {error}
            </div>
          )}

          <button
            type="submit"
            className="login-btn"
            disabled={loading || !username.trim()}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div className="login-footer">
          <p>Don't have an account? Contact admin.</p>
        </div>
      </div>
    </div>
  );
}

export default Login;

