import React, { useState, useEffect } from "react";
import api from "./axiosConfig";
import "./modal.css"; // 🧠 Upewnij się, że importujesz styl

const API_BASE = 'http://localhost:8080/api/v1';

function EditItemModal({ isOpen, onClose, auction, onSuccess }) {
    const [item, setItem] = useState({
        itemName: '',
        itemDescription: '',
        itemPrice: 0
    });

    useEffect(() => {
        if (auction) {
            setItem({
                itemName: auction.itemName || '',
                itemDescription: auction.itemDescription || '',
                itemPrice: auction.itemPrice || 0
            });
        }
    }, [auction]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("token");

        try {
            await api.put(`${API_BASE}/auctions/${auction.id}/item/edit`, item, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            alert("✅ Zaktualizowano przedmiot.");
            onClose();
            onSuccess(); // odśwież aukcje
        } catch (err) {
            console.error("❌ Błąd edycji:", err);
            alert("Błąd edycji przedmiotu.");
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal-overlay">
            <div className="modal-window">
                <button onClick={onClose} className="modal-close">✖</button>
                <h3>✏️ Edytuj przedmiot</h3>
                <form onSubmit={handleSubmit}>
                    <label className="form-label">Nazwa</label>
                    <input
                        type="text"
                        placeholder="Nazwa przedmiotu"
                        value={item.itemName}
                        onChange={(e) => setItem({ ...item, itemName: e.target.value })}
                        required
                    />
                    <label className="form-label">Opis</label>
                    <input
                        type="text"
                        placeholder="Opis przedmiotu"
                        value={item.itemDescription}
                        onChange={(e) => setItem({ ...item, itemDescription: e.target.value })}
                        required
                    />
                    <label className="form-label">Cena</label>
                    <input
                        type="number"
                        placeholder="Cena wywoławcza"
                        value={item.itemPrice}
                        onChange={(e) => setItem({ ...item, itemPrice: parseFloat(e.target.value) || 0 })}
                        required
                    />
                    <button type="submit">💾 Zapisz zmiany</button>
                </form>

            </div>
        </div>
    );
}

export default EditItemModal;
