import { fetchClient } from './client';
import { FileItem } from '../stores/editorStore';

export const validateApi = {
  validateSemantic: (files: FileItem[]): Promise<{fileId: string, message: string}[]> =>
    fetchClient('/validate/semantic', { method: 'POST', body: JSON.stringify({ files }) }),
};

