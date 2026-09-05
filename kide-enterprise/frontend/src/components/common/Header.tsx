import React from 'react';
import { useLocation } from 'react-router-dom';

const Header = () => {
  const location = useLocation();
  
  const getPageTitle = () => {
    const path = location.pathname;
    if (path.startsWith('/dashboard')) return 'Dashboard';
    if (path.match(/^\/projects\/\d+$/)) return 'Project Workspace';
    if (path.startsWith('/projects')) return 'Projects';
    if (path.startsWith('/settings')) return 'Settings';
    return 'KIDE Enterprise';
  };

  return (
    <header className="h-16 bg-[#1a1a2e] border-b border-[#0f3460] flex items-center px-6 shrink-0">
      <h2 className="text-xl font-semibold text-white">{getPageTitle()}</h2>
    </header>
  );
};

export default Header;

