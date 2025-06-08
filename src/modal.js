// Modal.js
import React from 'react';
import './modal.css';

function Modal({ isOpen, onClose, children }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-window">
        <button className="modal-close" onClick={onClose}>✖</button>
        {children}
      </div>
    </div>
  );
}

export default Modal;
