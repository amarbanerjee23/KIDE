import { useAuthStore } from '../stores/authStore';
import { MncModel } from '../types/models';

const fetchText = async (endpoint: string, body: any) => {
  const token = useAuthStore.getState().token;
  const res = await fetch(`/api/v1${endpoint}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(body)
  });
  if (!res.ok) throw new Error('Export failed');
  return res.text();
};

export const exportApi = {
  exportJson: async (model: MncModel['model']): Promise<string> => {
    return JSON.stringify(model, null, 2);
  },
  exportDsl: async (model: MncModel['model']): Promise<string> => {
    return fetchText('/export/dsl', model);
  },
  exportPython: async (model: MncModel['model']): Promise<string> => {
    return fetchText('/export/python', model);
  }
};

