import { Outlet } from 'react-router-dom';
import Sidebar from '../components/common/Sidebar';
import Header from '../components/common/Header';

const AppLayout = () => {
  return (
    <div className="flex h-screen bg-background text-white">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-auto bg-[#1a1a2e]">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AppLayout;

