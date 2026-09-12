import React from 'react';
import { Outlet, useMatch } from 'react-router-dom';
import Sidebar from '../components/common/Sidebar';
import Header from '../components/common/Header';

const AppLayout: React.FC = () => {
  // Use React Router's official route matching instead of brittle URL regex string inspection
  // Matches /projects/:id with any id format (numeric, UUID, slug, nested routes)
  const isProjectWorkspace = Boolean(useMatch({ path: '/projects/:id', end: false }));

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-[#0b0f19] text-gray-200">
      {/* Region A: Collapsible Global Sidebar */}
      <Sidebar />
      
      {/* Main Column: Content Area */}
      <div className="flex-1 flex flex-col min-w-0 min-h-0 overflow-hidden">
        {/* On regular pages (Dashboard, Projects list, Settings), show global top header.
            On Project Workspace (/projects/:id), the Project Header (Region B) is rendered inside the workspace. */}
        {!isProjectWorkspace && <Header />}

        <main className="flex-1 flex flex-col min-w-0 min-h-0 overflow-hidden bg-[#0b0f19]">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AppLayout;
