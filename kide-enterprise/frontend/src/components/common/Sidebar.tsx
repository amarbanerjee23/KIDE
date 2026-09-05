import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, FolderOpen, Settings, LogOut } from 'lucide-react';
import { useAuthStore } from '../../stores/authStore';

const Sidebar = () => {
  const { user, logout } = useAuthStore();

  const navItems = [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/projects', icon: FolderOpen, label: 'Projects' },
    { to: '/settings', icon: Settings, label: 'Settings' },
  ];

  return (
    <div className="w-64 bg-[#1a1a2e] border-r border-[#0f3460] flex flex-col">
      <div className="h-16 flex items-center px-6 border-b border-[#0f3460]">
        <h1 className="text-2xl font-bold text-white tracking-wider">
          KIDE <span className="text-[#16c79a]">Enterprise</span>
        </h1>
      </div>
      
      <nav className="flex-1 py-6 px-3 space-y-2">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center px-3 py-2.5 rounded-md transition-colors ${
                isActive 
                  ? 'bg-[#0f3460] text-white' 
                  : 'text-gray-400 hover:text-white hover:bg-[#16213e]'
              }`
            }
          >
            <item.icon className="w-5 h-5 mr-3" />
            <span className="font-medium">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-[#0f3460]">
        <div className="flex items-center mb-4 px-2">
          <div className="w-8 h-8 rounded-full bg-[#16c79a] flex items-center justify-center text-background font-bold mr-3">
            {user?.full_name?.charAt(0) || 'U'}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-white truncate">{user?.full_name}</p>
            <p className="text-xs text-gray-400 truncate">{user?.email}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="w-full flex items-center px-3 py-2 text-sm text-gray-400 hover:text-white hover:bg-[#16213e] rounded-md transition-colors"
        >
          <LogOut className="w-4 h-4 mr-3" />
          Logout
        </button>
      </div>
    </div>
  );
};

export default Sidebar;

