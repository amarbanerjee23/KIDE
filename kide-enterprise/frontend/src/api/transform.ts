import { TransformResult } from '../types/models';

const API_BASE = '/api/v1';

export const transformData = async (payload: any): Promise<TransformResult> => {
  try {
    const res = await fetch(`${API_BASE}/transform`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });
    
    if (!res.ok) {
      throw new Error(`Transform failed with status ${res.status}`);
    }
    
    return await res.json();
  } catch (error) {
    console.error('Failed to transform:', error);
    return {
      model: null,
      warnings: [],
      validation_errors: [String(error)]
    };
  }
};
