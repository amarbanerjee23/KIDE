import { TransformResult } from '../types/models';
import { fetchClient } from './client';

export const transformData = async (payload: any): Promise<TransformResult> => {
  try {
    let bodyPayload: any;
    if (payload.workspace || payload.activity_diagram) {
      bodyPayload = payload;
    } else if (payload.activities || payload.name) {
      bodyPayload = { activity_diagram: payload };
    } else {
      bodyPayload = { workspace: payload };
    }

    const data = await fetchClient('/transform/', {
      method: 'POST',
      body: JSON.stringify(bodyPayload),
    });
    
    return {
      model: data.model || data.mnc_model || null,
      warnings: data.warnings || [],
      validation_errors: data.validation_errors || data.errors || []
    };
  } catch (error: any) {
    console.error('Failed to transform:', error);
    return {
      model: null,
      warnings: [],
      validation_errors: [error.message || String(error)]
    };
  }
};



export const transformWorkspace = async (files: Array<{ name: string; content: string }>, _activeFileId?: string): Promise<TransformResult> => {
  const workspacePayload: Record<string, string> = {};
  for (const f of files) {
    workspacePayload[f.name] = f.content;
  }
  return transformData({ workspace: workspacePayload });
};
