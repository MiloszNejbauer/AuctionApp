import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';
import EditItemModal from './EditItemModal';

const API_BASE = 'http://localhost:8080/api/v1';

function AuctionList() {
  const [auctions, setAuctions] = useState([]);
  const [expandedAuctionId, setExpandedAuctionId] = useState(null);
  const [bidAmounts, setBidAmounts] = useState({});
  const [countdowns, setCountdowns] = useState({});
  const [currentUsername, setCurrentUsername] = useState(null);
  const [editAuction, setEditAuction] = useState(null); // auction to edit



  useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    if (storedUsername) {
      setCurrentUsername(storedUsername);
    }
  }, []);

  const fetchAuctions = () => {
    fetch(`${API_BASE}/auctions`)
      .then(res => res.json())
      .then(data => setAuctions(data))
      .catch(err => console.error('Błąd pobierania aukcji:', err));
  };

  // Ustawianie interwału do aktualizacji liczników
  useEffect(() => {
    const updateCountdowns = () => {
      const now = new Date().getTime();
      const newCountdowns = {};

      auctions.forEach(auction => {
        if (!auction.endTime || !auction.timestamp) {
          newCountdowns[auction.id] = null;
          return;
        }

        const end = new Date(auction.endTime).getTime();
        const remaining = end - now;

        if (remaining > 0) {
          const hours = Math.floor(remaining / (1000 * 60 * 60));
          const minutes = Math.floor((remaining % (1000 * 60 * 60)) / (1000 * 60));
          const seconds = Math.floor((remaining % (1000 * 60)) / 1000);
          newCountdowns[auction.id] = `${hours}h ${minutes}m ${seconds}s`;
        } else {
          newCountdowns[auction.id] = "⛔ Zakończona";
        }
      });

      setCountdowns(newCountdowns);
    };

    updateCountdowns();
    const interval = setInterval(updateCountdowns, 1000);
    return () => clearInterval(interval);
  }, [auctions]);


  useEffect(() => {
    fetchAuctions();
    const fetchInterval = setInterval(fetchAuctions, 10000); // aktualizacja aukcji co 10 sek.
    return () => clearInterval(fetchInterval);
  }, []);

  const toggleAuction = (auctionId) => {
    setExpandedAuctionId(prevId => (prevId === auctionId ? null : auctionId));
  };

  const deleteAuction = async (auctionId) => {
    if (!window.confirm('Czy na pewno chcesz usunąć tę aukcję?')) return;
    const token = localStorage.getItem("token");
    try {
      await axios.delete(`${API_BASE}/auctions/${auctionId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchAuctions();
    } catch (err) {
      console.error('Błąd usuwania aukcji:', err);
    }
  };

  const placeBid = async (auctionId) => {
    const token = localStorage.getItem("token");
    const amount = bidAmounts[auctionId];

    if (!token) {
      alert("Musisz być zalogowany, aby licytować.");
      return;
    }

    if (!amount || isNaN(amount) || amount <= 0) {
      alert("Wprowadź poprawną kwotę oferty.");
      return;
    }

    try {
      await axios.post(`${API_BASE}/auctions/${auctionId}/bid`, {
        amount: parseFloat(amount)
      }, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      alert("✅ Licytacja przyjęta!");
      fetchAuctions();
    } catch (err) {
      if (err.response && err.response.data) {
        alert("❌ " + err.response.data);
      } else {
        alert("❌ Błąd podczas licytacji");
      }
      console.error(err);
    }
  };

  return (
    <div className="auction-list">
      <h2>📋 Lista aukcji</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {auctions.map((auction) => {
          const isEnded = countdowns[auction.id] === "⛔ Zakończona";
          return (
            <li
              key={auction.id}
              style={{
                border: '1px solid #ccc',
                marginBottom: '10px',
                padding: '10px',
                backgroundColor: isEnded ? '#f8d7da' : 'white',
                opacity: isEnded ? 0.6 : 1,
                position: 'relative'
              }}
            >
              {/* Napis "Aukcja zakończona!" */}
              {isEnded && (
                <div className='auction-ended-banner'>
                  Aukcja zakończona!
                </div>
              )}

              <div className="auction-header">
                <div className="auction-info-left" onClick={() => toggleAuction(auction.id)} style={{ cursor: 'pointer' }}>
                  <h3>{auction.auctionName}</h3> <h4>{auction.currentBid || auction.itemPrice} zł</h4>
                  <div className="category">({auction.category})</div>
                  <p>👤 {auction.createdByUsername}</p>
                </div>

                <div className="auction-info-right">
                  <p>📅 {new Date(auction.timestamp).toLocaleString()}</p>
                  {auction.timestamp && auction.endTime ? (
                    <>
                      <p>⏱️ Czas: {
                        (() => {
                          const start = new Date(auction.timestamp);
                          const end = new Date(auction.endTime);
                          const diffMs = end - start;
                          const totalSeconds = Math.floor(diffMs / 1000);
                          const hours = Math.floor(totalSeconds / 3600);
                          const minutes = Math.floor((totalSeconds % 3600) / 60);

                          if (hours > 0 && minutes > 0) return `${hours}h ${minutes}m`;
                          if (hours > 0) return `${hours}h`;
                          return `${minutes} minut`;
                        })()
                      }</p>
                      <p>⌛ Pozostały czas: {countdowns[auction.id] || '⏳'}</p>
                    </>
                  ) : (
                    <em>Brak czasu</em>
                  )}

                  {currentUsername === auction.createdByUsername && (
                    <button className="delete-button" onClick={() => deleteAuction(auction.id)}>
                      🗑️ Usuń aukcję
                    </button>
                  )}
                  {currentUsername === auction.createdByUsername && (
                    <button
                      className="delete-button"
                      onClick={() => setEditAuction(auction)}
                    >
                      ✏️ Edytuj
                    </button>
                  )}
                </div>
              </div>

              {expandedAuctionId === auction.id && (
                <div className="auction-details-content">
                  <p>
                    🧩 <strong>{auction.itemName}</strong> – {auction.itemDescription}<br />
                    Cena startowa: {auction.itemPrice} zł<br />
                    Aktualna oferta: {auction.currentBid || auction.itemPrice} zł
                  </p>

                  {!isEnded ? (
                    <div className="bid-controls">
                      <input
                        type="number"
                        placeholder="Twoja oferta"
                        onChange={(e) =>
                          setBidAmounts({ ...bidAmounts, [auction.id]: parseFloat(e.target.value) || 0 })
                        }
                      />
                      <button onClick={() => placeBid(auction.id)}>
                        💸 Licytuj
                      </button>
                    </div>
                  ) : (
                    <em>Licytacja zakończona</em>
                  )}
                </div>
              )}

            </li>
          );
        })}
      </ul>
      <EditItemModal
        isOpen={!!editAuction}
        onClose={() => setEditAuction(null)}
        auction={editAuction}
        onSuccess={fetchAuctions}
      />
    </div>
  );
}

export default AuctionList;
