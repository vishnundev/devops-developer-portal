import { createContext, useContext, useMemo, useState } from 'react'
import api from '../api/api'

const AuthContext = createContext(null)
const TOKEN_KEY = 'devops_portal_token'
const USER_KEY = 'devops_portal_user'

function readStoredUser() {
  try {
    const storedUser = localStorage.getItem(USER_KEY)
    return storedUser ? JSON.parse(storedUser) : null
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser)

  const login = async (email, password) => {
    const response = await api.post('/auth/login', { email, password })
    const payload = response.data

    if (!payload.success || !payload.data?.accessToken) {
      throw new Error(payload.message || 'Unable to sign in')
    }

    const { accessToken, tokenType, expiresIn, userId, name, role } = payload.data
    const authenticatedUser = { id: userId, name, email, role, tokenType, expiresIn }
    localStorage.setItem(TOKEN_KEY, accessToken)
    localStorage.setItem(USER_KEY, JSON.stringify(authenticatedUser))
    setUser(authenticatedUser)
    return authenticatedUser
  }

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }

  const hasRole = (...roles) => Boolean(user?.role && roles.includes(user.role))

  const value = useMemo(() => ({
    user,
    role: user?.role ?? null,
    isAuthenticated: Boolean(user && localStorage.getItem(TOKEN_KEY)),
    login,
    logout,
    hasRole,
  }), [user])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }

  return context
}
