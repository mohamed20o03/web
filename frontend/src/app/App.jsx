/**
 * Root application component.
 * Sets up routing and global providers.
 * 
 * @module app/App
 * @component
 * 
 * @example
 * import App from './app/App';
 * 
 * ReactDOM.createRoot(root).render(<App />);
 * 
 * @returns {JSX.Element} Application root with routing
 */

import React from "react";
import { RouterProvider } from "react-router-dom";
import { Providers } from "./providers";
import { router } from "./router";

/**
 * App - Root application component.
 * Wraps the router with authentication and other global providers.
 * 
 * @component
 */
export default function App() {
  return (
    <Providers>
      <RouterProvider router={router} />
    </Providers>
  );
}
