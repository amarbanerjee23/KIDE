export interface PlatformStats {
  mrr_cents: number;
  mrr_usd: number;
  arr_cents: number;
  arr_usd: number;
  active_subscribers: number;
  active_trials: number;
  trials_expiring_soon: number;
  conversion_rate_pct: number;
  churn_rate_pct: number;
  tier_distribution: Record<string, number>;
  total_ai_tokens_used: number;
  total_projects: number;
  total_organizations: number;
  total_users: number;
}

export interface AdminOrganization {
  id: number;
  name: string;
  slug: string;
  owner_email?: string;
  owner_name?: string;
  plan_tier: 'community' | 'team' | 'enterprise';
  subscription_status: string;
  is_trial: boolean;
  trial_days_remaining: number;
  projects_count: number;
  members_count: number;
  ai_tokens_used: number;
  created_at?: string;
}

export interface AdminPlanOverridePayload {
  plan_tier: string;
  extend_trial_days?: number;
}

export interface FeatureFlag {
  id: number;
  key: string;
  name: string;
  description?: string;
  is_enabled: boolean;
  minimum_tier: 'community' | 'team' | 'enterprise';
  allowed_org_ids: number[];
  rollout_percentage: number;
  created_at?: string;
  updated_at?: string;
}

export interface FeatureFlagCreatePayload {
  key: string;
  name: string;
  description?: string;
  is_enabled?: boolean;
  minimum_tier?: string;
  allowed_org_ids?: number[];
  rollout_percentage?: number;
}

export interface FeatureFlagUpdatePayload {
  name?: string;
  description?: string;
  is_enabled?: boolean;
  minimum_tier?: string;
  allowed_org_ids?: number[];
  rollout_percentage?: number;
}
