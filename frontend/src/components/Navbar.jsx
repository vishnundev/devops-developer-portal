import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const initials = user?.name?.split(' ').map((part) => part[0]).join('').slice(0, 2).toUpperCase() || 'U'

  const handleLogout = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <header className="navbar">
      <div>
        <p className="eyebrow">Amazon Seller Dashboard</p>
        <h1>Developer Operations</h1>
      </div>
      <div className="account-area">
        <div className="avatar" aria-hidden="true">{initials}</div>
        <div className="account-details">
          <strong>{user?.name}</strong>
          <span>{user?.role}</span>
        </div>
        <button className="logout-button" type="button" onClick={handleLogout}>Log out</button>
      </div>
    </header>
  )
}
