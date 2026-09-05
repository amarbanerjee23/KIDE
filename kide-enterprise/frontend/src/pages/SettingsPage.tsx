import React from 'react';
import { useAuthStore } from '../stores/authStore';
import Button from '../components/common/Button';
import Input from '../components/common/Input';

const SettingsPage = () => {
  const user = useAuthStore(state => state.user);

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-white mb-8">Settings</h1>
      
      <div className="space-y-8">
        <section className="bg-surface border border-accent rounded-lg p-6">
          <h2 className="text-xl font-semibold text-white mb-4">Profile Information</h2>
          <div className="space-y-4 max-w-md">
            <Input label="Full Name" defaultValue={user?.full_name} disabled />
            <Input label="Email Address" defaultValue={user?.email} disabled />
            <Input label="Role" defaultValue={user?.role} disabled />
            <Button className="mt-4">Update Profile</Button>
          </div>
        </section>

        <section className="bg-surface border border-accent rounded-lg p-6">
          <h2 className="text-xl font-semibold text-white mb-4">Organization</h2>
          <div className="space-y-4 max-w-md">
            <Input label="Organization Name" defaultValue={user?.org_name || ''} />
            <Button className="mt-4">Save Organization</Button>
          </div>
        </section>

        <section className="bg-surface border border-accent rounded-lg p-6">
          <h2 className="text-xl font-semibold text-white mb-4">API Keys</h2>
          <p className="text-gray-400 text-sm mb-4">Generate API keys to use KIDE Enterprise via external services or CLI tools.</p>
          <div className="border border-dashed border-gray-600 rounded p-4 text-center text-gray-500">
            No API keys generated yet.
          </div>
          <Button variant="secondary" className="mt-4">Generate New Key</Button>
        </section>
      </div>
    </div>
  );
};

export default SettingsPage;

