import { useEffect, useState } from 'react'
import api from '../api/api'

export default function MetricsPage() {
  const [metrics, setMetrics] = useState([])
  const [loading, setLoading] = useState(true)

  const fetchMetrics = async () => {
    try {
      const response = await api.get('/metrics')
      setMetrics(response.data.data)
    } catch (error) {
      console.error('Failed to fetch metrics', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchMetrics()
    const interval = setInterval(fetchMetrics, 30000) // 30s Heartbeat
    return () => clearInterval(interval)
  }, [])

  return (
    <section>
      <div className="page-heading" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <p className="eyebrow">Platform telemetry</p>
          <h2>Metrics</h2>
          <p>Inspect CPU, memory, request, error, and uptime snapshots collected by the portal.</p>
        </div>
        <button className="btn-action" onClick={fetchMetrics} style={{ padding: '8px 16px', borderRadius: '5px', cursor: 'pointer' }}>
          Refresh Data
        </button>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Service Name</th>
              <th>CPU Usage</th>
              <th>Memory Usage</th>
              <th>Uptime (Seconds)</th>
              <th>Total Requests</th>
              <th>Captured At</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (

            <tr>
            <td colSpan="6">Loading...</td>
            </tr>

            ) : metrics.length === 0 ? (

            <tr>
            <td colSpan="6">No Metrics Found</td>
            </tr>

            ) : (

            metrics.map(metric => (

            <tr key={metric.id}>

            <td>{metric.serviceName}</td>

            <td>{metric.cpuUsage}%</td>

            <td>{metric.memoryUsage}%</td>

            <td>{metric.uptime}</td>

            <td>{metric.requestCount}</td>

            <td>{new Date(metric.capturedAt).toLocaleString()}</td>

            </tr>

            ))

            )}

            </tbody>
        </table>
      </div>
    </section>
  )
}   