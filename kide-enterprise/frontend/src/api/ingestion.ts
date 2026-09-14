import { fetchClient } from './client';
import { useAuthStore } from '../stores/authStore';
import {
  IngestionJob,
  StagedKnowledgeArtifact,
  NotificationItem,
  NotificationStats,
  SampleTemplate
} from '../types/ingestion';

export const ingestionApi = {
  getTemplates: (): Promise<SampleTemplate[]> =>
    fetchClient('/knowledge/ingest/templates'),

  ingestSample: (sampleId: string): Promise<IngestionJob> =>
    fetchClient('/knowledge/ingest/sample', {
      method: 'POST',
      body: JSON.stringify({ sample_id: sampleId })
    }),

  submitRaw: (data: { filename: string; file_type: string; content: string }): Promise<IngestionJob> =>
    fetchClient('/knowledge/ingest/submit-raw', {
      method: 'POST',
      body: JSON.stringify(data)
    }),

  uploadDocument: async (file: File): Promise<IngestionJob> => {
    const token = useAuthStore.getState().token;
    const formData = new FormData();
    formData.append('file', file);

    const headers: Record<string, string> = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch('/api/v1/knowledge/ingest/upload', {
      method: 'POST',
      headers,
      body: formData
    });

    if (!response.ok) {
      const err = await response.json().catch(() => ({}));
      throw new Error(err.detail || 'Failed to upload document');
    }

    return response.json();
  },

  getJobs: (limit: number = 50): Promise<IngestionJob[]> =>
    fetchClient(`/knowledge/ingest/jobs?limit=${limit}`),

  getJob: (jobId: number): Promise<IngestionJob> =>
    fetchClient(`/knowledge/ingest/jobs/${jobId}`),

  getJobStagedArtifacts: (jobId: number): Promise<StagedKnowledgeArtifact[]> =>
    fetchClient(`/knowledge/ingest/jobs/${jobId}/staged`),

  getAllStagedArtifacts: (status?: string): Promise<StagedKnowledgeArtifact[]> => {
    const query = status ? `?status=${status}` : '';
    return fetchClient(`/knowledge/ingest/staged${query}`);
  },

  getStagedArtifact: (artifactId: number): Promise<StagedKnowledgeArtifact> =>
    fetchClient(`/knowledge/ingest/staged/${artifactId}`),

  approveArtifact: (artifactId: number, notes?: string): Promise<StagedKnowledgeArtifact> =>
    fetchClient(`/knowledge/ingest/staged/${artifactId}/approve`, {
      method: 'POST',
      body: JSON.stringify({ notes })
    }),

  rejectArtifact: (artifactId: number, notes?: string): Promise<StagedKnowledgeArtifact> =>
    fetchClient(`/knowledge/ingest/staged/${artifactId}/reject`, {
      method: 'POST',
      body: JSON.stringify({ notes })
    }),

  importArtifactToProject: (
    artifactId: number,
    projectId: number
  ): Promise<{ message: string; imported_files: Array<{ filename: string; file_type: string; bytes: number }> }> =>
    fetchClient(`/knowledge/ingest/staged/${artifactId}/import-to-project/${projectId}`, {
      method: 'POST'
    }),

  getNotifications: (limit: number = 50): Promise<NotificationItem[]> =>
    fetchClient(`/notifications?limit=${limit}`),

  getNotificationStats: (): Promise<NotificationStats> =>
    fetchClient('/notifications/stats'),
};

