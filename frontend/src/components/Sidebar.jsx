import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const navigationItems = [
  { to: '/dashboard', label: 'Dashboard', symbol: '▦', roles: ['ADMIN', 'DEVELOPER', 'VIEWER'] },
  { to: '/services', label: 'Services', symbol: '◈', roles: ['ADMIN', 'DEVELOPER', 'VIEWER'] },
  { to: '/environments', label: 'Environments', symbol: '◇', roles: ['ADMIN', 'DEVELOPER'] },
  { to: '/deployments', label: 'Deployments', symbol: '↟', roles: ['ADMIN', 'DEVELOPER'] },
  { to: '/metrics', label: 'Metrics', symbol: '◒', roles: ['ADMIN', 'DEVELOPER', 'VIEWER'] },
  { to: '/notifications', label: 'Notifications', symbol: '◉', roles: ['ADMIN', 'DEVELOPER'] },
  { to: '/users', label: 'Users', symbol: '♙', roles: ['ADMIN'] },
]

export default function Sidebar() {
  const { role } = useAuth()
  const visibleItems = navigationItems.filter((item) => item.roles.includes(role))

  return (
    <aside className="sidebar">
      <NavLink className="brand" to="/dashboard" aria-label="DevOps Portal dashboard">
        <span className="brand-mark">D</span>
        <span>DevOps Portal</span>
      </NavLink>

      <nav className="sidebar-nav" aria-label="Primary navigation">
        <p className="nav-caption">Workspace</p>
        {visibleItems.map((item) => (
          <NavLink
            className={({ isActive }) => `nav-item${isActive ? ' nav-item-active' : ''}`}
            key={item.to}
            to={item.to}
          >
            <span className="nav-symbol" aria-hidden="true">{item.symbol}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <span className="status-dot" aria-hidden="true" />
        System monitoring active
      </div>
    </aside>
  )
}
