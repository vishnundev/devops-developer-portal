import { useEffect, useState } from 'react'
import api from '../api/api'

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([])

  const fetchNotifications = async () => {
    try {
      const response = await api.get('/notifications?unreadOnly=false')
      setNotifications(response.data.data.content)
    } catch (error) {
      console.error('Failed to fetch notifications', error)
    }
  }

  useEffect(() => { 
    fetchNotifications() 
    // ADDED: 10-second polling heartbeat
    const interval = setInterval(fetchNotifications, 10000)
    return () => clearInterval(interval)
  }, [])

  const markAsRead = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`)
      fetchNotifications()
    } catch (err) { console.error(err) }
  }

  return (
    <section>
      <div className="page-heading">
        <div>
          <p className="eyebrow">System Alerts</p>
          <h2>Notifications</h2>
        </div>
      </div>
      <div className="notification-list">
        {notifications.length === 0 ? <p>No notifications.</p> : notifications.map((notif) => (
          <div key={notif.id} className={`notification-card ${!notif.read ? 'unread' : ''}`}>
            <div className="notification-header">
              <span className={`status-badge status-${notif.severity.toLowerCase()}`}>{notif.severity}</span>
              <span>{new Date(notif.createdAt).toLocaleString()}</span>
            </div>
            <p className="notification-message">{notif.message}</p>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '8px' }}>
              <span style={{ fontSize: '0.8rem', color: '#64748b' }}>Service: {notif.serviceName}</span>
              {!notif.read && (
                <button className="btn-action" onClick={() => markAsRead(notif.id)}>Mark as read</button>
              )}
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}