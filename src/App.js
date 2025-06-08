import React, { useState, useEffect, useRef } from "react";
import AuctionList from "./AuctionList";
import AuctionForm from "./AuctionForm";
import AuthForm from "./AuthForm";
import Modal from "./modal"; // tylko do aukcji
import "./App.css";

function App() {
  const [showAuctionForm, setShowAuctionForm] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);

  const username = localStorage.getItem("username");
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    window.location.reload();
  };

  return (
    <div>
      {/* Toolbar */}
      <div className="toolbar">
        <h1 className="main-title">Mega Aukcje PL</h1>
        <div className="toolbar-buttons">
          <button onClick={() => setShowAuctionForm(prev => !prev)}>
            ➕ Stwórz aukcję
          </button>

          {/* Dropdown button */}
          <div className="user-dropdown-container" ref={dropdownRef}>
            <button onClick={() => setShowDropdown(prev => !prev)}>
              {username ? `👤 ${username}` : "🔐 Zaloguj się"}
            </button>

            {showDropdown && (
              <div className="dropdown-menu">
                {username ? (
                  <button onClick={handleLogout}>🚪 Wyloguj</button>
                ) : (
                  <AuthForm
                    onSuccess={() => {
                      setShowDropdown(false);
                      window.location.reload();
                    }}
                  />
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Modal do tworzenia aukcji */}
      <Modal isOpen={showAuctionForm} onClose={() => setShowAuctionForm(false)}>
  <AuctionForm onAuctionCreated={() => {
    setShowAuctionForm(false);
    window.location.reload();
  }} />
</Modal>

      <h1 className="page-title">Aukcyjne Aukcje</h1>

      <AuctionList />
    </div>
  );
}

export default App;
