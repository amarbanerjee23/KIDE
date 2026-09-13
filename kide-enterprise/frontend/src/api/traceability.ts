import { fetchClient } from './client';
import {
  TraceabilityMatrixResponse,
  ImpactAnalysisRequest,
  ImpactAnalysisResponse,
  ReconfigurationRequest,
  ReconfigurationProposal,
} from '../types/traceability';

export async function getProjectTraceabilityMatrix(
  projectId: number
): Promise<TraceabilityMatrixResponse> {
  return fetchClient(`/traceability/${projectId}/matrix`);
}

export async function getBufferTraceabilityMatrix(
  files: Record<string, string>
): Promise<TraceabilityMatrixResponse> {
  return fetchClient('/traceability/matrix', {
    method: 'POST',
    body: JSON.stringify({ files }),
  });
}

export async function analyzeProjectImpact(
  projectId: number,
  request: ImpactAnalysisRequest
): Promise<ImpactAnalysisResponse> {
  return fetchClient(`/traceability/${projectId}/impact`, {
    method: 'POST',
    body: JSON.stringify(request),
  });
}

export async function analyzeBufferImpact(
  files: Record<string, string>,
  request: ImpactAnalysisRequest
): Promise<ImpactAnalysisResponse> {
  return fetchClient('/traceability/impact', {
    method: 'POST',
    body: JSON.stringify({ files, ...request }),
  });
}

export async function reconfigureProjectCapability(
  projectId: number,
  request: ReconfigurationRequest
): Promise<ReconfigurationProposal> {
  return fetchClient(`/traceability/${projectId}/reconfigure`, {
    method: 'POST',
    body: JSON.stringify(request),
  });
}

export async function reconfigureBufferCapability(
  files: Record<string, string>,
  request: ReconfigurationRequest
): Promise<ReconfigurationProposal> {
  return fetchClient('/traceability/reconfigure', {
    method: 'POST',
    body: JSON.stringify({ files, ...request }),
  });
}

