import { useLocation } from 'react-router-dom'

const pageContent = {
  '/services': ['Services', 'Service operations', 'Registered backend modules, lifecycle controls, and status changes are managed here.'],
  '/environments': ['Environments', 'Environment assignment', 'Assign services to development, testing, staging, and production environments.'],
  '/deployments': ['Deployments', 'Release history', 'Review deployment status, version history, and environment-specific rollout records.'],
  '/metrics': ['Metrics', 'Platform telemetry', 'Inspect CPU, memory, request, error, and uptime snapshots collected by the portal.'],
  '/notifications': ['Notifications', 'System alerts', 'Review service-down, resource-threshold, and deployment alert activity.'],
  '/users': ['Users', 'Access management', 'Manage portal user accounts and their ADMIN, DEVELOPER, or VIEWER permissions.'],
}

export default function WorkspacePage() {
  const location = useLocation()
  const [title, eyebrow, description] = pageContent[location.pathname] || ['Workspace', 'Operations', 'Select an area from the navigation.']

  return (
    <section className="workspace-page">
      <p className="eyebrow">{eyebrow}</p>
      <h2>{title}</h2>
      <p className="workspace-description">{description}</p>
    </section>
  )
}
