/**
 * LoginPage Component Tests
 * Tests form rendering, validation, and submission behavior
 */
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import LoginPage from './LoginPage'
import * as authApi from './auth.api'
import { AuthProvider } from '../../core/auth/auth.context'

// Mock the auth API
vi.mock('./auth.api', () => ({
  loginRequest: vi.fn()
}))

// Mock useNavigate
const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate
  }
})

// Wrapper component with router and auth context
const renderWithRouter = (component) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  )
}

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders login form with email and password fields', () => {
    renderWithRouter(<LoginPage />)
    
    expect(screen.getByPlaceholderText(/student@eng.psu.edu.eg/i)).toBeInTheDocument()
    expect(screen.getByPlaceholderText(/••••••••/)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /log in/i })).toBeInTheDocument()
  })

  it('renders sign up link', () => {
    renderWithRouter(<LoginPage />)
    
    const signupLink = screen.getByText(/don't have an account/i)
    expect(signupLink).toBeInTheDocument()
  })

  it('shows validation error for empty email', async () => {
    renderWithRouter(<LoginPage />)
    
    const loginButton = screen.getByRole('button', { name: /log in/i })
    fireEvent.click(loginButton)
    
    await waitFor(() => {
      // Form should not submit without email (browser validation)
      expect(authApi.loginRequest).not.toHaveBeenCalled()
    })
  })

  it('submits form with valid credentials', async () => {
    const mockResponse = {
      ok: true,
      data: {
        token: 'fake-jwt-token',
        id: 1,
        email: 'test@eng.psu.edu.eg',
        firstName: 'Test',
        role: 'student',
        status: 'APPROVED'
      }
    }
    
    authApi.loginRequest.mockResolvedValue(mockResponse)
    
    renderWithRouter(<LoginPage />)
    
    const emailInput = screen.getByPlaceholderText(/student@eng.psu.edu.eg/i)
    const passwordInput = screen.getByPlaceholderText(/••••••••/)
    const loginButton = screen.getByRole('button', { name: /log in/i })
    
    fireEvent.change(emailInput, { target: { value: 'test@eng.psu.edu.eg' } })
    fireEvent.change(passwordInput, { target: { value: 'password123' } })
    fireEvent.click(loginButton)
    
    await waitFor(() => {
      expect(authApi.loginRequest).toHaveBeenCalledWith({
        identifier: 'test@eng.psu.edu.eg',
        password: 'password123'
      })
    })
  })

  it('displays error message on login failure', async () => {
    authApi.loginRequest.mockResolvedValue({
      ok: false,
      error: { message: 'Invalid credentials' }
    })
    
    renderWithRouter(<LoginPage />)
    
    const emailInput = screen.getByPlaceholderText(/student@eng.psu.edu.eg/i)
    const passwordInput = screen.getByPlaceholderText(/••••••••/)
    const loginButton = screen.getByRole('button', { name: /log in/i })
    
    fireEvent.change(emailInput, { target: { value: 'wrong@eng.psu.edu.eg' } })
    fireEvent.change(passwordInput, { target: { value: 'wrongpass' } })
    fireEvent.click(loginButton)
    
    await waitFor(() => {
      const errorMessage = screen.queryByText(/Invalid credentials/i)
      expect(errorMessage).toBeInTheDocument()
    })
  })

  it('disables submit button while loading', async () => {
    authApi.loginRequest.mockImplementation(() => new Promise(resolve => setTimeout(() => resolve({
      ok: true,
      data: { token: 'fake', id: 1, email: 'test@eng.psu.edu.eg', firstName: 'Test', role: 'student', status: 'APPROVED' }
    }), 100)))
    
    renderWithRouter(<LoginPage />)
    
    const emailInput = screen.getByPlaceholderText(/student@eng.psu.edu.eg/i)
    const passwordInput = screen.getByPlaceholderText(/••••••••/)
    const loginButton = screen.getByRole('button', { name: /log in/i })
    
    fireEvent.change(emailInput, { target: { value: 'test@eng.psu.edu.eg' } })
    fireEvent.change(passwordInput, { target: { value: 'password123' } })
    fireEvent.click(loginButton)
    
    expect(loginButton).toBeDisabled()
  })

  it('navigates to profile page after successful student login', async () => {
    const mockResponse = {
      ok: true,
      data: {
        token: 'fake-jwt-token',
        id: 1,
        email: 'test@eng.psu.edu.eg',
        firstName: 'Test',
        role: 'student',
        status: 'APPROVED'
      }
    }
    
    authApi.loginRequest.mockResolvedValue(mockResponse)
    
    renderWithRouter(<LoginPage />)
    
    const emailInput = screen.getByPlaceholderText(/student@eng.psu.edu.eg/i)
    const passwordInput = screen.getByPlaceholderText(/••••••••/)
    const loginButton = screen.getByRole('button', { name: /log in/i })
    
    fireEvent.change(emailInput, { target: { value: 'test@eng.psu.edu.eg' } })
    fireEvent.change(passwordInput, { target: { value: 'password123' } })
    fireEvent.click(loginButton)
    
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/me', { replace: true })
    })
  })

  it('navigates to admin dashboard after successful admin login', async () => {
    const mockResponse = {
      ok: true,
      data: {
        token: 'fake-jwt-token',
        id: 1,
        email: 'admin@eng.psu.edu.eg',
        firstName: 'Admin',
        role: 'ADMIN',
        status: 'APPROVED'
      }
    }
    
    authApi.loginRequest.mockResolvedValue(mockResponse)
    
    renderWithRouter(<LoginPage />)
    
    const emailInput = screen.getByPlaceholderText(/student@eng.psu.edu.eg/i)
    const passwordInput = screen.getByPlaceholderText(/••••••••/)
    const loginButton = screen.getByRole('button', { name: /log in/i })
    
    fireEvent.change(emailInput, { target: { value: 'admin@eng.psu.edu.eg' } })
    fireEvent.change(passwordInput, { target: { value: 'admin123' } })
    fireEvent.click(loginButton)
    
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/admin/users', { replace: true })
    })
  })

  it('navigates to status page for pending user', async () => {
    const mockResponse = {
      ok: true,
      data: {
        token: 'fake-jwt-token',
        id: 1,
        email: 'pending@eng.psu.edu.eg',
        firstName: 'Pending',
        role: 'student',
        status: 'PENDING'
      }
    }
    
    authApi.loginRequest.mockResolvedValue(mockResponse)
    
    renderWithRouter(<LoginPage />)
    
    const emailInput = screen.getByPlaceholderText(/student@eng.psu.edu.eg/i)
    const passwordInput = screen.getByPlaceholderText(/••••••••/)
    const loginButton = screen.getByRole('button', { name: /log in/i })
    
    fireEvent.change(emailInput, { target: { value: 'pending@eng.psu.edu.eg' } })
    fireEvent.change(passwordInput, { target: { value: 'password123' } })
    fireEvent.click(loginButton)
    
    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/status', { replace: true })
    })
  })
})
