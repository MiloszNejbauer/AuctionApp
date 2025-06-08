import React, { useEffect, useRef, useState } from "react";
import "./userDropdown.css";

function UserDropdown({ onLoginClick, onRegisterClick, onLogout }) {
  const [isOpen, setIsOpen] = useState(false);
  const username = localStorage.getItem("username");
  const dropdownRef = useRef(null);

  const toggleDropdown = () => setIsOpen(prev => !prev);

  const handleClickOutside = (e) => {
    if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="user-dropdown" ref={dropdownRef}>
      <button onClick={toggleDropdown}>
        {username ? `👤 ${username}` : "🔐 Zaloguj się"}
      </button>

      {isOpen && (
        <div className="dropdown-menu">
          {!username ? (
            <>
              <button onClick={() => { onLoginClick(); setIsOpen(false); }}>🔑 Zaloguj się</button>
            </>
          ) : (
            <button onClick={() => { onLogout(); setIsOpen(false); }}>🚪 Wyloguj się</button>
          )}
        </div>
      )}
    </div>
  );
}

export default UserDropdown;
