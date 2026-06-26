import { useEffect, useState } from 'react'
import api from '../api/api'

export default function EnvironmentsPage() {
  const [environments, setEnvironments] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchEnvironments = async () => {
      try {
        const response = await api.get('/environments')
        // Assuming paginated response based on your other endpoints
        setEnvironments(response.data.data.content || response.data.data)
      } catch (error) {
        console.error('Failed to fetch environments', error)
      } finally {
        setLoading(false)
      }
    }
    fetchEnvironments()
  }, [])

  return (
    <section>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Environment Assignment</p>
          <h2>Environments</h2>
          <p>Manage development, staging, and production environments.</p>
        </div>
      </div>
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Environment Name</th>
              <th>Description</th>
              <th>Health</th>
            </tr>
          </thead>
          <tbody>
            {loading ? <tr><td colSpan="3">Loading...</td></tr> : 
             environments.length === 0 ? <tr><td colSpan="3">No environments configured.</td></tr> : 
             environments.map((env) => (
              <tr key={env.id}>
                <td><strong>{env.name}</strong></td>
                <td>{env.description}</td>
                <td><span className="status-badge status-running">Healthy</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}