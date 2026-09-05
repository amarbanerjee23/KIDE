import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '../stores/authStore';
import { projectsApi } from '../api/projects';
import ProjectCard from '../components/common/ProjectCard';
import Button from '../components/common/Button';
import { FolderPlus, Play, LayoutDashboard } from 'lucide-react';

const DashboardPage = () => {
  const user = useAuthStore(state => state.user);
  const { data: projects = [] } = useQuery({
    queryKey: ['projects'],
    queryFn: projectsApi.listProjects,
  });

  const recentProjects = [...projects].sort((a, b) => 
    new Date(b.updated_at).getTime() - new Date(a.updated_at).getTime()
  ).slice(0, 3);

  return (
    <div className="p-8 max-w-7xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">Hello, {user?.full_name}</h1>
        <p className="text-gray-400">Welcome back to KIDE Enterprise.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
        <div className="bg-surface border border-accent p-6 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-gray-400 font-medium">Total Projects</h3>
            <LayoutDashboard className="text-highlight w-5 h-5" />
          </div>
          <p className="text-3xl font-bold text-white mt-4">{projects.length}</p>
        </div>
        <div className="bg-surface border border-accent p-6 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-gray-400 font-medium">Total Transforms (Today)</h3>
            <Play className="text-highlight w-5 h-5" />
          </div>
          <p className="text-3xl font-bold text-white mt-4">0</p>
        </div>
        <div className="bg-surface border border-accent p-6 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-gray-400 font-medium">Active Files</h3>
            <FolderPlus className="text-highlight w-5 h-5" />
          </div>
          <p className="text-3xl font-bold text-white mt-4">{projects.reduce((sum, p) => sum + p.file_count, 0)}</p>
        </div>
      </div>

      <div className="mb-8 flex items-center justify-between">
        <h2 className="text-xl font-semibold text-white">Recent Projects</h2>
        <div className="space-x-4">
          <Button variant="secondary" onClick={() => {}}>
            <Play className="w-4 h-4 mr-2" /> Try Example Transform
          </Button>
          <Button onClick={() => {}}>
            <FolderPlus className="w-4 h-4 mr-2" /> New Project
          </Button>
        </div>
      </div>

      {recentProjects.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {recentProjects.map(project => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      ) : (
        <div className="bg-surface border border-dashed border-accent rounded-lg p-12 text-center">
          <p className="text-gray-400 mb-4">No projects yet. Get started by creating one.</p>
          <Button><FolderPlus className="w-4 h-4 mr-2" /> Create First Project</Button>
        </div>
      )}
    </div>
  );
};

export default DashboardPage;

