import { ProposedPatch } from '../api/ai';

export type TraceLinkType =
  | 'specifies'
  | 'requires_capability'
  | 'requires_operation'
  | 'context_model'
  | 'implements_interface'
  | 'transitions_to'
  | 'synthesizes_state'
  | 'generates_target';

export type ImpactRiskLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface TraceabilityLink {
  source_symbol: string;
  source_type: string;
  source_file: string;
  target_symbol: string;
  target_type: string;
  target_file: string;
  link_type: TraceLinkType;
  description: string;
}

export interface TraceabilityMatrixRow {
  symbol: string;
  symbol_type: string;
  filename: string;
  stage: number;
  upstream_symbols: string[];
  downstream_symbols: string[];
  links: TraceabilityLink[];
}

export interface TraceabilityMatrixResponse {
  project_id?: number;
  total_symbols: number;
  total_links: number;
  coverage_percentage: number;
  rows: TraceabilityMatrixRow[];
  layer_counts: Record<string, number>;
}

export interface ImpactedItem {
  symbol: string;
  symbol_type: string;
  filename: string;
  stage: number;
  impact_reason: string;
  risk: ImpactRiskLevel;
}

export interface ImpactAnalysisRequest {
  target_symbol?: string;
  target_file?: string;
  action?: 'modify' | 'delete' | 'rename';
  new_name?: string;
}

export interface ImpactAnalysisResponse {
  target_symbol: string;
  target_type: string;
  action: string;
  risk_level: ImpactRiskLevel;
  impacted_symbols_count: number;
  impacted_items: ImpactedItem[];
  affected_activities: string[];
  affected_states: string[];
  broken_transitions: string[];
  affected_code_generators: string[];
  breaking_hazards: string[];
  recommended_mitigations: string[];
}

export interface ReconfigurationRequest {
  deprecated_capability: string;
  replacement_capability: string;
  target_activities?: string[];
}

export interface ReconfigurationProposal {
  status: 'success' | 'no_change_needed' | 'incompatible';
  explanation: string;
  deprecated_capability: string;
  replacement_capability: string;
  affected_files: string[];
  patches: ProposedPatch[];
  safety_verification: Record<string, any>;
}

