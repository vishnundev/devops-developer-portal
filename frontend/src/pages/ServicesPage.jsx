import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import api from '../api/api'

export default function ServicesPage() {
  const { hasRole } = useAuth()
  const [services, setServices] = useState([])
  const [loading, setLoading] = useState(true)
  
  const [showAddForm, setShowAddForm] = useState(false)
  const [newService, setNewService] = useState({
    name: '',
    description: '',
    moduleName: '',
    version: 'v1.0.0',
    port: '',
    status: 'RUNNING'
  })

  const fetchServices = async () => {
    try {
      const response = await api.get('/services')
      setServices(response.data.data.content)
    } catch (error) {
      console.error('Failed to fetch services', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchServices()
    const interval = setInterval(fetchServices, 10000) // 10s Heartbeat
    return () => clearInterval(interval)
  }, [])

  const handleAction = async (id, action) => {
    try {
      await api.patch(`/services/${id}/status`, { action })
      fetchServices()
    } catch (error) {
      alert(error.response?.data?.message || 'Action failed')
    }
  }

  const handleAddService = async (e) => {
    e.preventDefault();
    try {
      await api.post('/services', { ...newService, port: parseInt(newService.port) });
      setShowAddForm(false);
      setNewService({ name: '', description: '', moduleName: '', version: 'v1.0.0', port: '', status: 'RUNNING' });
      fetchServices();
    } catch (error) {
      alert(error.response?.data?.message || 'Failed to create service');
    }
  }

  const canManage = hasRole('ADMIN', 'DEVELOPER')
  const isAdmin = hasRole('ADMIN')

  return (
    <section>
      <div className="page-heading" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <p className="eyebrow">Service Management</p>
          <h2>Backend Services</h2>
          <p>Monitor and manage Amazon Seller Dashboard modules.</p>
        </div>
        {isAdmin && (
          <button className="btn-action" style={{ padding: '10px 20px', backgroundColor: '#007bff', color: 'white', borderRadius: '5px' }} onClick={() => setShowAddForm(!showAddForm)}>
            {showAddForm ? 'Cancel' : '+ Add Service'}
          </button>
        )}
      </div>

      {showAddForm && (
        <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px', marginBottom: '20px', border: '1px solid #ddd' }}>
          <h3>Register New Service</h3>
          <form onSubmit={handleAddService} style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', marginTop: '10px' }}>
            <input type="text" placeholder="Service Name (e.g. Inventory)" required value={newService.name} onChange={e => setNewService({...newService, name: e.target.value})} />
            <input type="text" placeholder="Module Name (e.g. inventory-core)" required value={newService.moduleName} onChange={e => setNewService({...newService, moduleName: e.target.value})} />
            <input type="number" placeholder="Port (e.g. 8081)" required value={newService.port} onChange={e => setNewService({...newService, port: e.target.value})} />
            <button type="submit" style={{ padding: '8px 16px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Save Service</button>
          </form>
        </div>
      )}

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Service Name</th>
              <th>Module</th>
              <th>Version</th>
              <th>Port</th>
              <th>Status</th>
              {canManage && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {loading ? <tr><td colSpan="6">Loading services...</td></tr> : services.length === 0 ? <tr><td colSpan="6">No services found.</td></tr> : services.map((service) => (
                <tr key={service.id}>
                  <td><strong>{service.name}</strong></td>
                  <td>{service.moduleName}</td>
                  <td>v{service.version}</td>
                  <td>{service.port}</td>
                  <td><span className={`status-badge status-${service.status.toLowerCase()}`}>{service.status}</span></td>
                  {canManage && (
                    <td>
                      <div className="action-group">
                        <button className="btn-action" onClick={() => handleAction(service.id, 'START')} disabled={service.status === 'RUNNING'}>Start</button>
                        <button className="btn-action" onClick={() => handleAction(service.id, 'STOP')} disabled={service.status === 'STOPPED'}>Stop</button>
                      </div>
                    </td>
                  )}
                </tr>
              ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}