import { ValidationError, FileItem } from '../types/models';

const API_BASE = '/api/v1';

export const validateSemantic = async (files: FileItem[]): Promise<ValidationError[]> => {
  try {
    const payload = files.map(f => ({
      id: f.id,
      name: f.name,
      content: f.content,
      language: f.language
    }));

    const res = await fetch(`${API_BASE}/validate/semantic`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });
    
    if (!res.ok) {
      throw new Error(`Validation failed with status ${res.status}`);
    }
    
    return await res.json();
  } catch (error) {
    console.error('Failed to validate:', error);
    return [{ message: String(error), severity: 'error' }];
  }
};
