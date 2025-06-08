import React, { useState } from 'react';
import axios from 'axios';
import './AuthForm.css'; // stylowanie dropdowna

const API_BASE = 'http://localhost:8080/api/v1';

function AuthForm({ formType = "login", onSuccess, onSwitch }) {
  const [currentForm, setCurrentForm] = useState(formType);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [username, setUsername] = useState(""); // dla rejestracji
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (currentForm === "register") {
      try {
        await axios.post(`${API_BASE}/users/add`, {
          email,
          username,
          password
        });

        setMessage("✅ Rejestracja zakończona sukcesem!");
        setCurrentForm("login");
      } catch (err) {
        setMessage("❌ Błąd rejestracji.");
        console.error(err);
      }
    } else if (currentForm === "login") {
      try {
        const res = await axios.post(`${API_BASE}/users/login`, {
          email,
          password
        });

        const { token, username: returnedUsername, email: returnedEmail } = res.data;

        localStorage.setItem("token", token);
        localStorage.setItem("userEmail", returnedEmail);
        localStorage.setItem("username", returnedUsername);

        setMessage(`✅ Zalogowano jako: ${returnedEmail}`);
        if (onSuccess) onSuccess();
      } catch (err) {
        setMessage("❌ Nieprawidłowy email lub hasło.");
        console.error(err);
      }
    }
  };

  return (
    <div className="auth-dropdown-form">
      <form onSubmit={handleSubmit}>
        {currentForm === "register" && (
          <input
            type="text"
            placeholder="Nazwa użytkownika"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        )}
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Hasło"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">
          {currentForm === "login" ? "Zaloguj się" : "Zarejestruj się"}
        </button>
        {message && <p className="auth-message">{message}</p>}
      </form>

      <button
        className="switch-auth"
        onClick={() => setCurrentForm(currentForm === "login" ? "register" : "login")}
      >
        {currentForm === "login"
          ? "Nie masz konta? Zarejestruj się"
          : "Masz już konto? Zaloguj się"}
      </button>
    </div>
  );
}

export default AuthForm;
