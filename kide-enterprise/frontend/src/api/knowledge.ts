import { fetchClient } from './client';

export interface CatalogItem {
  id: string;
  name: string;
  category: string;
  specification_reference?: string;
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

export interface KnowledgeGraphNode {
  id: string;
  name: string;
  type: string;
  category: string;
  specification_reference?: string;
  source_file?: string;
  properties?: Record<string, any>;
}

export interface KnowledgeGraphEdge {
  id: string;
  source: string;
  target: string;
  type: string;
  label: string;
  properties?: Record<string, any>;
}

export interface KnowledgeGraphData {
  scope: 'global' | 'project';
  project_id?: number;
  project_name?: string;
  nodes: KnowledgeGraphNode[];
  edges: KnowledgeGraphEdge[];
  stats: Record<string, number>;
}

export interface CapabilityMatch {
  catalog_id: string;
  system_name: string;
  category: string;
  specification_reference?: string;
  score: number;
  devices: string[];
  capabilities: Array<{
    name: string;
    interface: string;
    commands: any[];
    events: any[];
  }>;
}

export interface StoreSummary {
  stats: {
    domains: number;
    devices: number;
    capabilities: number;
    operations: number;
    datamodels: number;
    workflows: number;
    activities: number;
    total_nodes: number;
    total_edges: number;
    [key: string]: number;
  };
  domains: Array<{
    id: string;
    name: string;
    category: string;
    specification_reference?: string;
    description: string;
    devices: string[];
    file_count: number;
  }>;
}

export interface StoreEntity {
  id: string;
  name: string;
  type: string;
  category: string;
  specification_reference?: string;
  source_file?: string;
  properties?: Record<string, any>;
  content_preview?: string;
}

export const knowledgeApi = {
  getCatalog: (): Promise<CatalogItem[]> =>
    fetchClient('/knowledge/catalog'),

  getCatalogItem: (catalogId: string): Promise<CatalogItem> =>
    fetchClient(`/knowledge/catalog/${catalogId}`),

  getGlobalGraph: (): Promise<KnowledgeGraphData> =>
    fetchClient('/knowledge/graph'),

  getProjectGraph: (projectId: number): Promise<KnowledgeGraphData> =>
    fetchClient(`/projects/${projectId}/knowledge-graph`),

  getStoreSummary: (): Promise<StoreSummary> =>
    fetchClient('/knowledge/store/summary'),

  getStoreEntities: (params?: { entity_type?: string; domain_id?: string; query?: string }): Promise<StoreEntity[]> => {
    const searchParams = new URLSearchParams();
    if (params?.entity_type && params.entity_type !== 'all') searchParams.append('entity_type', params.entity_type);
    if (params?.domain_id && params.domain_id !== 'all') searchParams.append('domain_id', params.domain_id);
    if (params?.query) searchParams.append('query', params.query);
    const qs = searchParams.toString();
    return fetchClient(`/knowledge/store/entities${qs ? `?${qs}` : ''}`);
  },

  getEntityDetail: (entityId: string): Promise<any> =>
    fetchClient(`/knowledge/store/entities/${encodeURIComponent(entityId)}`),

  importEntity: (projectId: number, entityId: string): Promise<{ message: string; imported_file: any; entity_name: string; entity_type: string }> =>
    fetchClient(`/projects/${projectId}/import-entity`, { method: 'POST', body: JSON.stringify({ entity_id: entityId }) }),

  matchCapabilities: (query: string): Promise<CapabilityMatch[]> =>
    fetchClient('/knowledge/match', { method: 'POST', body: JSON.stringify({ query }) }),

  exportGraph: async (projectId: number, format: 'json' | 'turtle' = 'turtle'): Promise<string> => {
    const res = await fetch(`/api/v1/projects/${projectId}/knowledge-graph/export?format=${format}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('kide_access_token') || ''}`
      }
    });
    return res.text();
  },

  importKnowledge: (projectId: number, catalogId: string): Promise<{ message: string; catalog_id: string; imported_files: any[] }> =>
    fetchClient(`/projects/${projectId}/import-knowledge`, { method: 'POST', body: JSON.stringify({ catalog_id: catalogId }) }),

  convertSpec: (data: { spec_type: string; content: string; name?: string }): Promise<{ spec_type: string; name: string; files: Record<string, string> }> =>
    fetchClient('/knowledge/convert-spec', { method: 'POST', body: JSON.stringify(data) }),
};
