import { fetchClient } from './client';
import { Project, ProjectFile } from '../types/models';

export const projectsApi = {
  listProjects: (): Promise<Project[]> => fetchClient('/projects'),
  createProject: (data: { name: string; description: string }): Promise<Project> => 
    fetchClient('/projects', { method: 'POST', body: JSON.stringify(data) }),
  getProject: (id: number): Promise<Project> => fetchClient(`/projects/${id}`),
  updateProject: (id: number, data: { name?: string; description?: string }): Promise<Project> => 
    fetchClient(`/projects/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteProject: (id: number): Promise<void> => fetchClient(`/projects/${id}`, { method: 'DELETE' }),
  
  addFile: (projectId: number, data: { filename: string; content: string; file_type?: string }): Promise<ProjectFile> =>
    fetchClient(`/projects/${projectId}/files`, { method: 'POST', body: JSON.stringify(data) }),
  getFile: (projectId: number, fileId: number): Promise<ProjectFile> =>
    fetchClient(`/projects/${projectId}/files/${fileId}`),
  deleteFile: (projectId: number, fileId: number): Promise<void> =>
    fetchClient(`/projects/${projectId}/files/${fileId}`, { method: 'DELETE' })
};

