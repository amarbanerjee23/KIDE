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
      body: JSON.stringify({ files: payload }),
    });
    
    if (!res.ok) {
      throw new Error(`Validation failed with status ${res.status}`);
    }
    
    const data = await res.json();
    if (Array.isArray(data)) {
      return data.map((item: any) => ({
        fileId: item.fileId || item.file_id,
        filename: item.filename,
        line: item.line,
        column: item.column,
        message: item.message,
        severity: item.severity || 'error',
        ruleId: item.ruleId || item.rule_id,
        symbol: item.symbol,
        suggestion: item.suggestion,
        quickFix: item.quickFix || item.quick_fix
      }));
    }
    return [];
  } catch (error) {
    console.error('Failed to validate:', error);
    return [{ message: String(error), severity: 'error' }];
  }
};

export const compileProjectIR = async (files: FileItem[]): Promise<any> => {
  const payload = files.map(f => ({
    id: f.id,
    name: f.name,
    content: f.content,
    language: f.language
  }));

  const res = await fetch(`${API_BASE}/validate/ir`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ files: payload }),
  });

  if (!res.ok) {
    throw new Error(`IR compilation failed with status ${res.status}`);
  }

  return await res.json();
};
