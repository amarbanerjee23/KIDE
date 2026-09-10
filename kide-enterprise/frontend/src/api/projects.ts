import { fetchClient } from './client';
import { Project, ProjectFile, ProjectTemplate } from '../types/models';

export const projectsApi = {
  listProjects: (): Promise<Project[]> => fetchClient('/projects'),
  createProject: (data: { name: string; description?: string }): Promise<Project> => 
    fetchClient('/projects', { method: 'POST', body: JSON.stringify(data) }),
  getProject: (id: number): Promise<Project> => fetchClient(`/projects/${id}`),
  updateProject: (id: number, data: { name?: string; description?: string }): Promise<Project> => 
    fetchClient(`/projects/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteProject: (id: number): Promise<void> => fetchClient(`/projects/${id}`, { method: 'DELETE' }),

  getTemplates: (): Promise<ProjectTemplate[]> => fetchClient('/projects/templates'),
  createFromTemplate: (data: { name: string; template: string; description?: string }): Promise<Project> =>
    fetchClient('/projects/from-template', { method: 'POST', body: JSON.stringify(data) }),
  seedExamples: (projectId: number, templateKey: string = 'industrial_cooling'): Promise<ProjectFile[]> =>
    fetchClient(`/projects/${projectId}/seed-examples?template_key=${templateKey}`, { method: 'POST' }),
  
  listFiles: (projectId: number): Promise<ProjectFile[]> =>
    fetchClient(`/projects/${projectId}/files`),
  addFile: (projectId: number, data: { filename: string; content: string; file_type?: string }): Promise<ProjectFile> =>
    fetchClient(`/projects/${projectId}/files`, { method: 'POST', body: JSON.stringify(data) }),
  getFile: (projectId: number, fileId: number | string): Promise<ProjectFile> =>
    fetchClient(`/projects/${projectId}/files/${fileId}`),
  updateFile: (projectId: number, fileId: number | string, data: { content?: string; filename?: string; file_type?: string }): Promise<ProjectFile> =>
    fetchClient(`/projects/${projectId}/files/${fileId}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteFile: (projectId: number, fileId: number | string): Promise<void> =>
    fetchClient(`/projects/${projectId}/files/${fileId}`, { method: 'DELETE' })
};


