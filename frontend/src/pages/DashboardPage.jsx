import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import api from '../api/api'

export default function DashboardPage() {
  const { user } = useAuth()
  const [stats, setStats] = useState({ total: 0, running: 0, stopped: 0, down: 0 })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchDashboardStats = async () => {
      try {
        const { data } = await api.get('/services?size=100')
        const services = data.data.content || []
        
        setStats({
          total: services.length,
          running: services.filter(s => s.status === 'RUNNING').length,
          stopped: services.filter(s => s.status === 'STOPPED').length,
          down: services.filter(s => s.status === 'DOWN').length,
        })
      } catch (error) {
        console.error('Failed to load dashboard stats', error)
      } finally {
        setLoading(false)
      }
    }
    
    fetchDashboardStats()
    const interval = setInterval(fetchDashboardStats, 10000) // 10s Heartbeat
    return () => clearInterval(interval)
  }, [])

  return (
    <section className="dashboard-page">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Operations overview</p>
          <h2>Workspace Dashboard</h2>
          <p>Real-time platform status and service metrics.</p>
        </div>
        <span className="role-badge">{user?.role}</span>
      </div>

      <div className="overview-grid">
        <article className="overview-card">
          <span className="overview-icon">◈</span>
          <h3>Total Services</h3>
          <p style={{ fontSize: '2rem', fontWeight: 'bold', color: '#1d2c43', marginTop: '10px' }}>
            {loading ? '-' : stats.total}
          </p>
        </article>
        <article className="overview-card">
          <span className="overview-icon" style={{ color: '#166534' }}>▶</span>
          <h3>Running Workloads</h3>
          <p style={{ fontSize: '2rem', fontWeight: 'bold', color: '#166534', marginTop: '10px' }}>
            {loading ? '-' : stats.running}
          </p>
        </article>
        <article className="overview-card">
          <span className="overview-icon" style={{ color: '#991b1b' }}>■</span>
          <h3>Stopped / Down</h3>
          <p style={{ fontSize: '2rem', fontWeight: 'bold', color: '#991b1b', marginTop: '10px' }}>
            {loading ? '-' : (stats.stopped + stats.down)}
          </p>
        </article>
      </div>
    </section>
  )
}