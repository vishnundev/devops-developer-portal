import { Navigate, Route, Routes } from 'react-router-dom'
import AppLayout from './components/AppLayout'
import ProtectedRoute from './components/ProtectedRoute'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './pages/LoginPage'
import WorkspacePage from './pages/WorkspacePage'
import ServicesPage from './pages/ServicesPage'
import DeploymentsPage from './pages/DeploymentsPage'
import NotificationsPage from './pages/NotificationsPage'
import MetricsPage from './pages/MetricsPage'
import EnvironmentsPage from './pages/EnvironmentsPage' // <-- Import new page

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/services" element={<ServicesPage />} />
          <Route path="/environments" element={<EnvironmentsPage />} /> {/* <-- Updated Route */}
          <Route path="/deployments" element={<DeploymentsPage />} />
          <Route path="/metrics" element={<MetricsPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/users" element={<WorkspacePage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}