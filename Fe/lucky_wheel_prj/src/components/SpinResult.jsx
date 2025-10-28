function SpinResult({ results }) {
  if (!results || results.length === 0) {
    return null;
  }

  return (
    <div className="spin-result">
      <h3>🎉 Congratulations! You won:</h3>
      <div className="result-list">
        {results.map((result, index) => (
          <div key={index} className="result-item">
            <div className="result-item-info">
              <div className="result-item-name">
                {result.rewardName}
              </div>
              <div className="result-item-type">
                Type: {result.rewardType}
              </div>
            </div>
            <div className="result-item-quantity">
              x{result.quantity}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default SpinResult;

