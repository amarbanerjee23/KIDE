import { fetchClient } from './client';
import { MncModel } from '../types/models';

export const exportApi = {
  exportJson: async (model: MncModel['model']): Promise<string> => {
    return JSON.stringify(model, null, 2);
  },
  exportDsl: async (model: MncModel['model']): Promise<string> => {
    const res = await fetchClient('/export/dsl', { method: 'POST', body: JSON.stringify(model) });
    return res.dsl;
  },
  exportPython: async (model: MncModel['model']): Promise<string> => {
    const res = await fetchClient('/export/python', { method: 'POST', body: JSON.stringify(model) });
    return res.code;
  }
};

