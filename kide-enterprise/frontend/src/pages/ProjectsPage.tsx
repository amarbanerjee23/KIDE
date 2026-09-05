import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '../api/projects';
import ProjectCard from '../components/common/ProjectCard';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import { Search, FolderPlus } from 'lucide-react';

const ProjectsPage = () => {
  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newProjectName, setNewProjectName] = useState('');
  const [newProjectDesc, setNewProjectDesc] = useState('');
  
  const queryClient = useQueryClient();

  const { data: projects = [], isLoading } = useQuery({
    queryKey: ['projects'],
    queryFn: projectsApi.listProjects,
  });

  const createMutation = useMutation({
    mutationFn: projectsApi.createProject,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      setIsModalOpen(false);
      setNewProjectName('');
      setNewProjectDesc('');
    }
  });

  const filteredProjects = projects.filter(p => 
    p.name.toLowerCase().includes(search.toLowerCase()) || 
    (p.description && p.description.toLowerCase().includes(search.toLowerCase()))
  );

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (newProjectName) {
      createMutation.mutate({ name: newProjectName, description: newProjectDesc });
    }
  };

  return (
    <div className="p-8 max-w-7xl mx-auto h-full flex flex-col">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
        <h1 className="text-3xl font-bold text-white">Projects</h1>
        <Button onClick={() => setIsModalOpen(true)}>
          <FolderPlus className="w-4 h-4 mr-2" /> New Project
        </Button>
      </div>

      <div className="mb-6 relative">
        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <Search className="h-5 w-5 text-gray-500" />
        </div>
        <Input 
          type="text"
          placeholder="Search projects..."
          className="pl-10 max-w-md"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center flex-1 text-gray-400">Loading projects...</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProjects.map(project => (
            <ProjectCard key={project.id} project={project} />
          ))}
          {filteredProjects.length === 0 && (
             <div className="col-span-full py-12 text-center text-gray-400 border border-dashed border-accent rounded-lg">
                No projects found.
             </div>
          )}
        </div>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Create New Project">
        <form onSubmit={handleCreate} className="space-y-4">
          <Input 
            label="Project Name" 
            required 
            value={newProjectName} 
            onChange={e => setNewProjectName(e.target.value)} 
          />
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Description</label>
            <textarea 
              className="w-full rounded-md border border-[#0f3460] bg-[#1a1a2e] px-3 py-2 text-sm text-white focus:ring-2 focus:ring-[#16c79a]"
              rows={3}
              value={newProjectDesc}
              onChange={e => setNewProjectDesc(e.target.value)}
            />
          </div>
          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="ghost" type="button" onClick={() => setIsModalOpen(false)}>Cancel</Button>
            <Button type="submit" isLoading={createMutation.isPending}>Create Project</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default ProjectsPage;

