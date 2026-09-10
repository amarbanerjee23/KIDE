import { ParseResult } from '../types/models';

const API_BASE = '/api/v1';

export const parseText = async (language: string, text: string): Promise<ParseResult> => {
  try {
    const res = await fetch(`${API_BASE}/parse/${language}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain',
      },
      body: text,
    });
    
    if (!res.ok) {
      throw new Error(`Parse failed with status ${res.status}`);
    }
    
    return await res.json();
  } catch (error) {
    console.error('Failed to parse:', error);
    return { ast: null, errors: [{ message: String(error), severity: 'error' }] };
  }
};
