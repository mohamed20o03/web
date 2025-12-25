/**
 * Common style patterns and presets for consistent UI across the application.
 * These objects can be spread into inline styles or converted to Tailwind classes.
 * 
 * @module styles
 */

/**
 * Glass morphism effect styles.
 * Creates a frosted glass appearance with backdrop blur.
 * 
 * @constant
 * @type {Object}
 * @example
 * // As inline style
 * <div style={glassEffect}>Content</div>
 * 
 * // Or use the Tailwind class: glass-card
 */
export const glassEffect = {
  background: 'rgba(255, 255, 255, 0.05)',
  backdropFilter: 'blur(16px)',
  WebkitBackdropFilter: 'blur(16px)',
  border: '1px solid rgba(255, 255, 255, 0.15)',
  boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.4)',
};

/**
 * Strong glass effect for emphasized containers like search bars.
 * 
 * @constant
 * @type {Object}
 */
export const glassEffectStrong = {
  background: 'rgba(255, 255, 255, 0.1)',
  backdropFilter: 'blur(12px)',
  WebkitBackdropFilter: 'blur(12px)',
  border: '1px solid rgba(255, 255, 255, 0.2)',
  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
};

/**
 * Card container with glass effect.
 * 
 * @constant
 * @type {Object}
 */
export const glassCard = {
  ...glassEffect,
  borderRadius: 24,
  color: 'white',
  width: '100%',
  textAlign: 'left',
};

/**
 * Input field styles with dark glass effect.
 * 
 * @constant
 * @type {Object}
 */
export const inputField = {
  width: '100%',
  padding: '12px 16px',
  background: 'rgba(0, 0, 0, 0.3)',
  border: '1px solid rgba(255, 255, 255, 0.1)',
  borderRadius: 8,
  color: 'white',
  fontSize: 15,
  outline: 'none',
};

/**
 * Primary button styles.
 * 
 * @constant
 * @type {Object}
 */
export const buttonPrimary = {
  padding: '12px 24px',
  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  color: 'white',
  border: 'none',
  borderRadius: 8,
  fontSize: 16,
  fontWeight: 600,
  cursor: 'pointer',
  transition: 'all 0.3s ease',
};

/**
 * Back/cancel button styles.
 * 
 * @constant
 * @type {Object}
 */
export const buttonBack = {
  display: 'inline-block',
  textDecoration: 'none',
  color: '#4de1ff',
  fontSize: 15,
  fontWeight: 'bold',
  background: 'rgba(0, 198, 255, 0.1)',
  padding: '10px 20px',
  borderRadius: 30,
  transition: '0.2s',
};

/**
 * Social media button factory function.
 * 
 * @function
 * @param {string} bg - Background color
 * @returns {Object} Button style object
 * @example
 * <a style={socialButton('#0077b5')} href="...">LinkedIn</a>
 */
export const socialButton = (bg) => ({
  display: 'inline-flex',
  alignItems: 'center',
  justifyContent: 'center',
  padding: '8px 20px',
  background: bg === '#24292e' ? 'rgba(255,255,255,0.15)' : bg,
  color: 'white',
  textDecoration: 'none',
  borderRadius: 10,
  fontSize: 14,
  fontWeight: 600,
  boxShadow: '0 4px 10px rgba(0,0,0,0.3)',
});

/**
 * Background page container with fixed image and overlay.
 * 
 * @function
 * @param {string} bgImage - Background image URL
 * @returns {Object} Style object for page container
 */
export const pageBackground = (bgImage) => ({
  minHeight: '100vh',
  backgroundImage: `linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.8)), url(${bgImage})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundAttachment: 'fixed',
});

/**
 * Flexbox centering container.
 * 
 * @constant
 * @type {Object}
 */
export const centerContainer = {
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  minHeight: '100vh',
};
