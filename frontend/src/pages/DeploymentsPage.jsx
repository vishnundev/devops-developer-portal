import { useEffect, useState } from 'react'
import api from '../api/api'
import { useAuth } from '../context/AuthContext'

export default function DeploymentsPage() {
  const { hasRole } = useAuth()
  const [deployments, setDeployments] = useState([])
  const [showDeployForm, setShowDeployForm] = useState(false)
  const [formData, setFormData] = useState({ serviceId: '', environmentId: '', version: '' })

  const fetchDeployments = async () => {
    try {
      const response = await api.get('/deployments')
      setDeployments(response.data.data.content)
    } catch (error) {
      console.error('Failed to fetch deployments', error)
    }
  }

  useEffect(() => { fetchDeployments() }, [])

  const handleDeploy = async (e) => {
    e.preventDefault()
    try {
      await api.post('/deployments', formData)
      setShowDeployForm(false)
      fetchDeployments()
    } catch (error) {
      alert(error.response?.data?.message || 'Deployment failed')
    }
  }

  return (
    <section>
      <div className="page-heading" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <p className="eyebrow">Release History</p>
          <h2>Deployments</h2>
        </div>
        {hasRole('ADMIN', 'DEVELOPER') && (
          <button className="btn-action btn-primary" onClick={() => setShowDeployForm(!showDeployForm)}>
            {showDeployForm ? 'Cancel' : 'New Deployment'}
          </button>
        )}
      </div>

      {showDeployForm && (
        <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px', marginBottom: '20px', border: '1px solid #e7ecf3' }}>
          <h3>Trigger Deployment</h3>
          <form onSubmit={handleDeploy} style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
            <input type="number" placeholder="Service ID" required onChange={e => setFormData({...formData, serviceId: e.target.value})} />
            <input type="number" placeholder="Environment ID" required onChange={e => setFormData({...formData, environmentId: e.target.value})} />
            <input type="text" placeholder="Version (e.g., 1.0.1)" required onChange={e => setFormData({...formData, version: e.target.value})} />
            <button type="submit" className="btn-action btn-primary">Deploy</button>
          </form>
        </div>
      )}

      <div className="table-container">
        {/* Keep your existing table markup here */}
        <table className="data-table">
          <thead>
            <tr>
              <th>Service</th>
              <th>Environment</th>
              <th>Version</th>
              <th>Time</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {deployments.length === 0 ? (
              <tr><td colSpan="5">No deployment history found.</td></tr>
            ) : (
              deployments.map((dep) => (
                <tr key={dep.id}>
                  <td><strong>{dep.serviceName}</strong></td>
                  <td>{dep.environmentName}</td>
                  <td>v{dep.version}</td>
                  <td>{new Date(dep.deploymentTime).toLocaleString()}</td>
                  <td><span className={`status-badge status-${dep.deploymentStatus.toLowerCase()}`}>{dep.deploymentStatus}</span></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  )
}