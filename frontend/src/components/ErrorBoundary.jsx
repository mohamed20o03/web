/**
 * Error Boundary component for catching and handling React errors.
 * Prevents white screen of death by displaying a fallback UI when errors occur.
 * 
 * @module components/ErrorBoundary
 * @component
 * 
 * @example
 * <ErrorBoundary>
 *   <App />
 * </ErrorBoundary>
 */

import React from 'react';

/**
 * ErrorBoundary - Catches JavaScript errors in child component tree.
 * 
 * Features:
 * - Catches errors during rendering, lifecycle methods, and constructors
 * - Displays user-friendly error message with reload option
 * - Logs error details to console for debugging
 * - Prevents app crash and white screen
 * 
 * @class ErrorBoundary
 * @extends React.Component
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Child components to wrap
 * 
 * @example
 * // Wrap entire app
 * ReactDOM.createRoot(root).render(
 *   <ErrorBoundary>
 *     <App />
 *   </ErrorBoundary>
 * );
 * 
 * @example
 * // Wrap specific feature
 * <ErrorBoundary>
 *   <AdminDashboard />
 * </ErrorBoundary>
 */
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  /**
   * Update state when an error is caught.
   * Called during the "render" phase, so side effects are not permitted.
   * 
   * @static
   * @param {Error} error - The error that was thrown
   * @returns {Object} New state object
   */
  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  /**
   * Log error details to console.
   * Called during the "commit" phase, so side effects are permitted.
   * 
   * @param {Error} error - The error that was thrown
   * @param {Object} errorInfo - Object with componentStack key
   */
  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
    this.setState({
      error,
      errorInfo,
    });
  }

  /**
   * Handle reload button click.
   * Clears error state and forces re-render.
   */
  handleReload = () => {
    this.setState({ hasError: false, error: null, errorInfo: null });
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      return (
        <div style={styles.container}>
          <div style={styles.card}>
            <div style={styles.iconContainer}>
              <span style={styles.icon}>⚠️</span>
            </div>
            
            <h1 style={styles.title}>Oops! Something went wrong</h1>
            
            <p style={styles.message}>
              We're sorry for the inconvenience. The application encountered an unexpected error.
            </p>
            
            <button onClick={this.handleReload} style={styles.button}>
              Reload Application
            </button>
            
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <details style={styles.details}>
                <summary style={styles.summary}>Error Details (Development Only)</summary>
                <pre style={styles.errorText}>
                  {this.state.error.toString()}
                  {this.state.errorInfo?.componentStack}
                </pre>
              </details>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

/**
 * Inline styles for ErrorBoundary component.
 * Using inline styles to avoid dependency on external stylesheets in case of catastrophic errors.
 * 
 * @private
 * @constant
 */
const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    padding: '20px',
    fontFamily: 'Inter, system-ui, sans-serif',
  },
  card: {
    background: 'white',
    borderRadius: '16px',
    padding: '48px',
    maxWidth: '600px',
    width: '100%',
    boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
    textAlign: 'center',
  },
  iconContainer: {
    marginBottom: '24px',
  },
  icon: {
    fontSize: '64px',
    display: 'inline-block',
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#1a202c',
    marginBottom: '16px',
    marginTop: 0,
  },
  message: {
    fontSize: '16px',
    color: '#4a5568',
    marginBottom: '32px',
    lineHeight: '1.6',
  },
  button: {
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    padding: '12px 32px',
    fontSize: '16px',
    fontWeight: '600',
    cursor: 'pointer',
    transition: 'transform 0.2s',
  },
  details: {
    marginTop: '32px',
    textAlign: 'left',
    background: '#f7fafc',
    borderRadius: '8px',
    padding: '16px',
  },
  summary: {
    cursor: 'pointer',
    fontWeight: '600',
    color: '#2d3748',
    marginBottom: '8px',
  },
  errorText: {
    fontSize: '12px',
    color: '#e53e3e',
    overflow: 'auto',
    maxHeight: '300px',
    background: '#fff5f5',
    padding: '12px',
    borderRadius: '4px',
    margin: 0,
  },
};

export default ErrorBoundary;
