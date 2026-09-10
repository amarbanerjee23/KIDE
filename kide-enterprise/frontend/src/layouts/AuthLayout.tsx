import { Outlet } from 'react-router-dom';

const AuthLayout = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4">
      <div className="w-full max-w-md bg-surface p-8 rounded-lg shadow-xl border border-accent">
        <div className="flex justify-center mb-8">
          <h1 className="text-3xl font-bold text-white tracking-wider">
            KIDE <span className="text-highlight">Enterprise</span>
          </h1>
        </div>
        <Outlet />
      </div>
    </div>
  );
};

export default AuthLayout;

