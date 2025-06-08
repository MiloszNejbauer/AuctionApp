// AuctionForm.js
import React, { useState } from "react";
import axios from "axios";
import './App.css';

const API_BASE = "http://localhost:8080/api/v1";

function AuctionForm({ onAuctionCreated }) {
  const [auction, setAuction] = useState({
    auctionName: "",
    category: "",
    durationMinutes: "",
    itemName: "",
    itemDescription: "",
    itemPrice: 0
  });

  const handleAuctionSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");

    try {
      await axios.post(`${API_BASE}/auctions/create`, {
        auctionName: auction.auctionName,
        category: auction.category,
        durationMinutes: parseInt(auction.durationMinutes),
        itemName: auction.itemName,
        itemDescription: auction.itemDescription,
        itemPrice: parseFloat(auction.itemPrice)
      }, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      alert("✅ Aukcja utworzona!");
      setAuction({
        auctionName: "",
        category: "",
        durationMinutes: "",
        itemName: "",
        itemDescription: "",
        itemPrice: 0
      });

      if (onAuctionCreated) onAuctionCreated(); // np. zamknięcie popupu
    } catch (err) {
      alert("❌ Błąd przy tworzeniu aukcji");
      console.error(err);
    }
  };

  return (
    <form onSubmit={handleAuctionSubmit}>
      <h2>🛠️ Stwórz aukcję</h2>

      <input
        placeholder="Nazwa aukcji"
        value={auction.auctionName}
        onChange={e => setAuction({ ...auction, auctionName: e.target.value })}
        required
      />

      <input
        placeholder="Kategoria"
        value={auction.category}
        onChange={e => setAuction({ ...auction, category: e.target.value })}
        required
      />

      <select
        value={auction.durationMinutes}
        onChange={e => setAuction({ ...auction, durationMinutes: e.target.value })}
        required
      >
        <option value="">⏱️ Czas trwania aukcji</option>
        <optgroup label="Minuty">
          <option value="5">5 minut</option>
          <option value="10">10 minut</option>
          <option value="15">15 minut</option>
          <option value="30">30 minut</option>
          <option value="55">55 minut</option>
        </optgroup>
        <optgroup label="Godziny">
          {Array.from({ length: 24 }, (_, i) => (
            <option key={i} value={(i + 1) * 60}>{i + 1}h</option>
          ))}
        </optgroup>
        <optgroup label="Dni">
          {Array.from({ length: 7 }, (_, i) => (
            <option key={i} value={(i + 1) * 24 * 60}>{i + 1} dzień</option>
          ))}
        </optgroup>
      </select>

      <hr />
      <h3>🎁 Przedmiot</h3>

      <input
        placeholder="Nazwa przedmiotu"
        value={auction.itemName}
        onChange={e => setAuction({ ...auction, itemName: e.target.value })}
        required
      />

      <input
        placeholder="Opis przedmiotu"
        value={auction.itemDescription}
        onChange={e => setAuction({ ...auction, itemDescription: e.target.value })}
        required
      />

      <input
        type="number"
        placeholder="Cena wywoławcza (zł)"
        value={auction.itemPrice}
        onChange={e => setAuction({ ...auction, itemPrice: parseFloat(e.target.value) || 0 })}
        required
      />

      <button type="submit">➕ Utwórz aukcję</button>
    </form>
  );
}

export default AuctionForm;
