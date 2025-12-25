/**
 * Toast notification system for displaying user feedback messages.
 * Provides success, error, warning, and info notification types.
 * 
 * @module components/ui/Toast
 */

import React, { createContext, useContext, useState, useCallback } from 'react';

/**
 * @typedef {Object} Toast
 * @property {string} id - Unique identifier for the toast
 * @property {('success'|'error'|'warning'|'info')} type - Toast type determining color scheme
 * @property {string} message - Message text to display
 * @property {number} [duration=5000] - Display duration in milliseconds
 */

const ToastContext = createContext(null);

/**
 * Hook to access toast notification functions.
 * Must be used within ToastProvider.
 * 
 * @function useToast
 * @returns {Object} Toast control functions
 * @returns {Function} returns.success - Show success toast
 * @returns {Function} returns.error - Show error toast
 * @returns {Function} returns.warning - Show warning toast
 * @returns {Function} returns.info - Show info toast
 * @returns {Function} returns.dismiss - Dismiss specific toast by ID
 * 
 * @throws {Error} If used outside ToastProvider
 * 
 * @example
 * function MyComponent() {
 *   const toast = useToast();
 *   
 *   const handleSave = async () => {
 *     try {
 *       await saveData();
 *       toast.success('Data saved successfully!');
 *     } catch (error) {
 *       toast.error(error.message || 'Failed to save data');
 *     }
 *   };
 *   
 *   return <button onClick={handleSave}>Save</button>;
 * }
 */
export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within ToastProvider');
  }
  return context;
}

/**
 * ToastProvider component - Provides toast notification context.
 * Manages toast state and rendering. Wrap your app or specific sections with this provider.
 * 
 * @component
 * @param {Object} props
 * @param {React.ReactNode} props.children - Child components
 * 
 * @example
 * // In App.jsx or main.jsx
 * <ToastProvider>
 *   <App />
 * </ToastProvider>
 */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  /**
   * Adds a new toast notification.
   * 
   * @private
   * @param {('success'|'error'|'warning'|'info')} type - Toast type
   * @param {string} message - Message to display
   * @param {number} [duration=5000] - Display duration in ms
   */
  const addToast = useCallback((type, message, duration = 5000) => {
    const id = `toast-${Date.now()}-${Math.random()}`;
    const toast = { id, type, message, duration };
    
    setToasts(prev => [...prev, toast]);
    
    if (duration > 0) {
      setTimeout(() => {
        setToasts(prev => prev.filter(t => t.id !== id));
      }, duration);
    }
  }, []);

  /**
   * Dismisses a toast by ID.
   * 
   * @private
   * @param {string} id - Toast ID to dismiss
   */
  const dismiss = useCallback((id) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  }, []);

  const value = {
    success: (message, duration) => addToast('success', message, duration),
    error: (message, duration) => addToast('error', message, duration),
    warning: (message, duration) => addToast('warning', message, duration),
    info: (message, duration) => addToast('info', message, duration),
    dismiss,
  };

  return (
    <ToastContext.Provider value={value}>
      {children}
      <ToastContainer toasts={toasts} onDismiss={dismiss} />
    </ToastContext.Provider>
  );
}

/**
 * ToastContainer component - Renders toast notifications.
 * 
 * @private
 * @component
 * @param {Object} props
 * @param {Toast[]} props.toasts - Array of toast objects
 * @param {Function} props.onDismiss - Dismiss handler
 */
function ToastContainer({ toasts, onDismiss }) {
  if (toasts.length === 0) return null;

  return (
    <div style={styles.container}>
      {toasts.map(toast => (
        <ToastItem key={toast.id} toast={toast} onDismiss={onDismiss} />
      ))}
    </div>
  );
}

/**
 * Individual toast notification item.
 * 
 * @private
 * @component
 * @param {Object} props
 * @param {Toast} props.toast - Toast object
 * @param {Function} props.onDismiss - Dismiss handler
 */
function ToastItem({ toast, onDismiss }) {
  const typeStyles = {
    success: {
      background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
      icon: '✓',
    },
    error: {
      background: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)',
      icon: '✕',
    },
    warning: {
      background: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
      icon: '⚠',
    },
    info: {
      background: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
      icon: 'ℹ',
    },
  };

  const style = typeStyles[toast.type] || typeStyles.info;

  return (
    <div style={{ ...styles.toast, background: style.background }}>
      <span style={styles.icon}>{style.icon}</span>
      <span style={styles.message}>{toast.message}</span>
      <button
        onClick={() => onDismiss(toast.id)}
        style={styles.closeButton}
        aria-label="Dismiss notification"
      >
        ×
      </button>
    </div>
  );
}

const styles = {
  container: {
    position: 'fixed',
    top: '20px',
    right: '20px',
    zIndex: 9999,
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
    maxWidth: '400px',
  },
  toast: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    padding: '16px 20px',
    borderRadius: '12px',
    color: 'white',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.3)',
    animation: 'slideIn 0.3s ease-out',
    minWidth: '300px',
  },
  icon: {
    fontSize: '20px',
    fontWeight: 'bold',
    flexShrink: 0,
  },
  message: {
    flex: 1,
    fontSize: '14px',
    lineHeight: '1.4',
  },
  closeButton: {
    background: 'transparent',
    border: 'none',
    color: 'white',
    fontSize: '24px',
    cursor: 'pointer',
    padding: '0',
    width: '24px',
    height: '24px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: '4px',
    opacity: 0.8,
    transition: 'opacity 0.2s',
  },
};

// Add animation keyframes globally (should be in CSS file ideally)
if (typeof document !== 'undefined') {
  const style = document.createElement('style');
  style.textContent = `
    @keyframes slideIn {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }
  `;
  document.head.appendChild(style);
}
