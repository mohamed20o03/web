/**
 * StudentDirectoryPage Component Tests
 * Tests student listing, filtering, and search functionality
 */
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import StudentDirectoryPage from './StudentDirectoryPage'
import * as publicApi from './public.api'

// Mock the public API
vi.mock('./public.api', () => ({
  getPublicUsers: vi.fn()
}))

// Wrapper component with router
const renderWithProviders = (component) => {
  return render(
    <BrowserRouter>
      {component}
    </BrowserRouter>
  )
}

// Mock student data matching API structure
const mockStudents = [
  {
    id: 1,
    firstName: 'Ahmed',
    lastName: 'Mohamed',
    faculty: 'Engineering',
    department: 'Computer Science',
    year: 3,
    profilePhotoUrl: null
  },
  {
    id: 2,
    firstName: 'Sara',
    lastName: 'Hassan',
    faculty: 'Engineering',
    department: 'Electrical Engineering',
    year: 2,
    profilePhotoUrl: null
  },
  {
    id: 3,
    firstName: 'Mohamed',
    lastName: 'Ali',
    faculty: 'Science',
    department: 'Physics',
    year: 4,
    profilePhotoUrl: null
  }
]

describe('StudentDirectoryPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders page title and search box', async () => {
    publicApi.getPublicUsers.mockResolvedValue([])

    renderWithProviders(<StudentDirectoryPage />)
    
    await waitFor(() => {
      expect(screen.getByText(/Student Directory/i)).toBeInTheDocument()
    })
    expect(screen.getByPlaceholderText(/search/i)).toBeInTheDocument()
  })

  it('displays loading state while fetching students', () => {
    publicApi.getPublicUsers.mockImplementation(() => new Promise(() => {}))

    renderWithProviders(<StudentDirectoryPage />)
    
    expect(screen.getByText(/loading/i)).toBeInTheDocument()
  })

  it('displays student cards when data is loaded', async () => {
    publicApi.getPublicUsers.mockResolvedValue(mockStudents)

    renderWithProviders(<StudentDirectoryPage />)
    
    await waitFor(() => {
      expect(screen.getByText(/Ahmed Mohamed/i)).toBeInTheDocument()
    })
    expect(screen.getByText(/Sara Hassan/i)).toBeInTheDocument()
    expect(screen.getByText(/Mohamed Ali/i)).toBeInTheDocument()
  })

  it('does not display student cards when fetch fails', async () => {
    publicApi.getPublicUsers.mockRejectedValue(new Error('Network error'))

    renderWithProviders(<StudentDirectoryPage />)
    
    await waitFor(() => {
      // Should not show loading after fetch completes
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument()
    })
    // Should not show any student cards
    expect(screen.queryByText(/Ahmed Mohamed/i)).not.toBeInTheDocument()
  })

  it('filters students by search query', async () => {
    publicApi.getPublicUsers.mockResolvedValue(mockStudents)

    renderWithProviders(<StudentDirectoryPage />)
    
    await waitFor(() => {
      expect(screen.getByText(/Ahmed Mohamed/i)).toBeInTheDocument()
    })

    const searchInput = screen.getByPlaceholderText(/search/i)
    fireEvent.change(searchInput, { target: { value: 'Sara' } })
    
    await waitFor(() => {
      expect(screen.getByText(/Sara Hassan/i)).toBeInTheDocument()
      expect(screen.queryByText(/Ahmed Mohamed/i)).not.toBeInTheDocument()
    })
  })

  it('displays department information for each student', async () => {
    publicApi.getPublicUsers.mockResolvedValue(mockStudents)

    renderWithProviders(<StudentDirectoryPage />)
    
    await waitFor(() => {
      expect(screen.getByText(/Computer Science/i)).toBeInTheDocument()
    })
    expect(screen.getByText(/Electrical Engineering/i)).toBeInTheDocument()
  })

  it('filters students by faculty dropdown', async () => {
    publicApi.getPublicUsers.mockResolvedValue(mockStudents)

    renderWithProviders(<StudentDirectoryPage />)
    
    await waitFor(() => {
      expect(screen.getByText(/Ahmed Mohamed/i)).toBeInTheDocument()
    })

    // Find the faculty filter dropdown
    const facultySelect = screen.getByRole('combobox')
    fireEvent.change(facultySelect, { target: { value: 'Science' } })
    
    await waitFor(() => {
      expect(screen.getByText(/Mohamed Ali/i)).toBeInTheDocument()
      expect(screen.queryByText(/Ahmed Mohamed/i)).not.toBeInTheDocument()
    })
  })
})
