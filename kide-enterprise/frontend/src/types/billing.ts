export type PlanTier = 'community' | 'team' | 'enterprise';

export interface Plan {
  tier: string;
  name: string;
  description?: string;
  price_monthly_cents: number;
  price_annual_cents: number;
  max_projects: number; // -1 for unlimited
  max_members: number;  // -1 for unlimited
  monthly_ai_tokens: number; // -1 for unlimited
  allowed_generators: string[];
  traceability_export: boolean;
  audit_retention_days: number;
}

export interface UsageSummary {
  projects_count: number;
  max_projects: number;
  members_count: number;
  max_members: number;
  ai_tokens_used: number;
  monthly_ai_tokens: number;
}

export interface Subscription {
  id: number;
  org_id: number;
  plan_tier: string;
  plan_name: string;
  status: string;
  current_period_start?: string;
  current_period_end?: string;
  trial_start?: string;
  trial_end?: string;
  is_trial: boolean;
  trial_days_remaining: number;
  cancel_at_period_end: boolean;
  usage: UsageSummary;
}

export interface CheckoutPayload {
  tier: PlanTier;
  billing_cycle?: 'monthly' | 'annual';
  success_url?: string;
  cancel_url?: string;
}

export interface CheckoutResponse {
  session_id: string;
  checkout_url: string;
}

export interface PortalResponse {
  portal_url: string;
}

export interface ActivateTrialResponse {
  status: string;
  plan_tier: string;
  trial_end: string;
  message: string;
}

