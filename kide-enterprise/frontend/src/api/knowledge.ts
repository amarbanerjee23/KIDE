import { fetchClient } from './client';

export interface CatalogItem {
  id: string;
  name: string;
  category: string;
  thesis_reference: string;
  description: string;
  tags: string[];
  devices: string[];
  file_count: number;
  files?: Array<{
    filename: string;
    file_type: string;
    content: string;
  }>;
}

export const knowledgeApi = {
  getCatalog: (): Promise<CatalogItem[]> =>
    fetchClient('/knowledge/catalog'),

  getCatalogItem: (catalogId: string): Promise<CatalogItem> =>
    fetchClient(`/knowledge/catalog/${catalogId}`),

  importKnowledge: (projectId: number, catalogId: string): Promise<{ message: string; catalog_id: string; imported_files: any[] }> =>
    fetchClient(`/projects/${projectId}/import-knowledge`, { method: 'POST', body: JSON.stringify({ catalog_id: catalogId }) }),

  convertSpec: (data: { spec_type: string; content: string; name?: string }): Promise<{ spec_type: string; name: string; files: Record<string, string> }> =>
    fetchClient('/knowledge/convert-spec', { method: 'POST', body: JSON.stringify(data) }),
};
