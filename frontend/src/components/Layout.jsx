import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FiGrid, FiTool, FiLogOut, FiTruck } from 'react-icons/fi';

function Layout() {
  const { user, logout } = useAuth();

  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="sidebar-logo-icon">
            <FiTruck />
          </div>
          <div>
            <h1>LogAp</h1>
            <span>Fleet Management</span>
          </div>
        </div>

        <nav className="sidebar-nav">
          <NavLink to="/" end className={({ isActive }) => isActive ? 'active' : ''}>
            <FiGrid />
            <span>Dashboard</span>
          </NavLink>
          <NavLink to="/manutencoes" className={({ isActive }) => isActive ? 'active' : ''}>
            <FiTool />
            <span>Manutenções</span>
          </NavLink>
        </nav>

        <div className="sidebar-user">
          <div className="sidebar-user-avatar">
            {getInitials(user?.nome)}
          </div>
          <div className="sidebar-user-info">
            <p>{user?.nome}</p>
            <span>{user?.email}</span>
          </div>
          <button className="sidebar-logout" onClick={logout} title="Sair">
            <FiLogOut />
          </button>
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
