import { fetchClient } from './client';
import { ActivityDiagram, TransformResult } from '../types/models';

export const transformApi = {
  transformActivity: (diagram: ActivityDiagram): Promise<TransformResult> =>
    fetchClient('/transform', { method: 'POST', body: JSON.stringify(diagram) }),
  getHistory: () => fetchClient('/transform/history'),
  getHistoryItem: (id: number) => fetchClient(`/transform/history/${id}`)
};

