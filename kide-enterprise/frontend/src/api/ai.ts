import { fetchClient } from './client';

export interface ChatMessage {
  id?: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
}

export interface ToolCallRecord {
  id: string;
  tool: string;
  input: Record<string, any>;
  output: any;
  status: string;
}

export interface ProposedPatch {
  filename: string;
  action: 'modify' | 'create' | 'delete';
  diff: string;
  new_content: string;
  rationale: string;
}

export interface AIChatResponse {
  session_id: string;
  provider: string;
  model: string;
  message: string;
  tool_calls: ToolCallRecord[];
  proposed_patches: ProposedPatch[];
  provenance_id?: number;
}

export interface ApplyPatchResponse {
  status: string;
  applied_files: string[];
  timestamp: string;
}

export interface AIProvenanceRecord {
  id: number;
  project_id: number;
  session_id: string;
  provider: string;
  model_name: string;
  user_prompt: string;
  tool_calls?: any[];
  assistant_response: string;
  proposed_patches?: any[];
  status: string;
  created_at: string;
}

export interface AIProvidersResponse {
  available_providers: string[];
  active_provider: string;
  active_model: string;
  has_gemini_key: boolean;
  has_openai_key: boolean;
  has_anthropic_key: boolean;
}

export async function sendAiMessage(
  projectId: number,
  messages: ChatMessage[],
  contextFile?: string,
  provider?: string,
  model?: string
): Promise<AIChatResponse> {
  return fetchClient('/ai/chat', {
    method: 'POST',
    body: JSON.stringify({
      project_id: projectId,
      messages,
      context_file: contextFile,
      provider,
      model,
    }),
  });
}

export async function applyAiPatch(
  projectId: number,
  provenanceId: number | undefined,
  patches: ProposedPatch[]
): Promise<ApplyPatchResponse> {
  return fetchClient('/ai/apply-patch', {
    method: 'POST',
    body: JSON.stringify({
      project_id: projectId,
      provenance_id: provenanceId,
      patches,
    }),
  });
}

export async function getAiProvenance(projectId: number): Promise<AIProvenanceRecord[]> {
  return fetchClient(`/ai/provenance/${projectId}`);
}

export async function getAiProviders(): Promise<AIProvidersResponse> {
  return fetchClient('/ai/providers');
}
