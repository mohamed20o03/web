/**
 * Auth Storage Tests
 * Tests session storage functionality
 */
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { authStorage } from './auth.storage'

describe('authStorage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    global.localStorage.getItem.mockReturnValue(null)
    global.localStorage.setItem.mockClear()
    global.localStorage.removeItem.mockClear()
  })

  describe('get', () => {
    it('returns null when no session exists', () => {
      global.localStorage.getItem.mockReturnValue(null)
      
      const session = authStorage.get()
      expect(session).toBeNull()
    })

    it('returns parsed session when valid JSON exists', () => {
      const mockSession = {
        token: 'test-token',
        email: 'test@eng.psu.edu.eg',
        role: 'STUDENT'
      }
      global.localStorage.getItem.mockReturnValue(JSON.stringify(mockSession))
      
      const session = authStorage.get()
      expect(session).toEqual(mockSession)
    })

    it('returns null when invalid JSON exists', () => {
      global.localStorage.getItem.mockReturnValue('invalid-json')
      
      const session = authStorage.get()
      expect(session).toBeNull()
    })
  })

  describe('set', () => {
    it('stores session in localStorage', () => {
      const mockSession = {
        token: 'test-token',
        email: 'test@eng.psu.edu.eg',
        role: 'STUDENT'
      }
      
      authStorage.set(mockSession)
      expect(global.localStorage.setItem).toHaveBeenCalledWith(
        'campuscard.session',
        JSON.stringify(mockSession)
      )
    })
  })

  describe('clear', () => {
    it('removes session from localStorage', () => {
      authStorage.clear()
      expect(global.localStorage.removeItem).toHaveBeenCalledWith('campuscard.session')
    })
  })

  describe('getToken', () => {
    it('returns token when session exists', () => {
      const mockSession = { token: 'test-token', email: 'test@eng.psu.edu.eg' }
      global.localStorage.getItem.mockReturnValue(JSON.stringify(mockSession))
      
      const token = authStorage.getToken()
      expect(token).toBe('test-token')
    })

    it('returns undefined when no session exists', () => {
      global.localStorage.getItem.mockReturnValue(null)
      
      const token = authStorage.getToken()
      expect(token).toBeUndefined()
    })
  })

  describe('getRole', () => {
    it('returns role when session exists', () => {
      const mockSession = { token: 'test-token', role: 'ADMIN' }
      global.localStorage.getItem.mockReturnValue(JSON.stringify(mockSession))
      
      const role = authStorage.getRole()
      expect(role).toBe('ADMIN')
    })

    it('returns undefined when no session exists', () => {
      global.localStorage.getItem.mockReturnValue(null)
      
      const role = authStorage.getRole()
      expect(role).toBeUndefined()
    })
  })

  describe('getUser', () => {
    it('returns session when exists', () => {
      const mockSession = { token: 'test-token', email: 'test@eng.psu.edu.eg', role: 'STUDENT' }
      global.localStorage.getItem.mockReturnValue(JSON.stringify(mockSession))
      
      const user = authStorage.getUser()
      expect(user).toEqual(mockSession)
    })

    it('returns null when no session exists', () => {
      global.localStorage.getItem.mockReturnValue(null)
      
      const user = authStorage.getUser()
      expect(user).toBeNull()
    })
  })
})
