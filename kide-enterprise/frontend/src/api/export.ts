import { MncModel } from '../types/models';

const API_BASE = '/api/v1';

const exportFormat = async (format: string, model: MncModel): Promise<string> => {
  try {
    const res = await fetch(`${API_BASE}/export/${format}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(model),
    });
    
    if (!res.ok) {
      throw new Error(`Export failed with status ${res.status}`);
    }
    
    return await res.text();
  } catch (error) {
    console.error(`Failed to export ${format}:`, error);
    return `Error: ${error}`;
  }
};

export const exportJson = (model: MncModel) => exportFormat('json', model);
export const exportDsl = (model: MncModel) => exportFormat('dsl', model);
export const exportPython = (model: MncModel) => exportFormat('python', model);
export const exportActivity = (model: MncModel) => exportFormat('activity', model);
export const exportCapability = (model: MncModel) => exportFormat('capability', model);
export const exportOperation = (model: MncModel) => exportFormat('operation', model);
export const exportDml = (model: MncModel) => exportFormat('dml', model);
