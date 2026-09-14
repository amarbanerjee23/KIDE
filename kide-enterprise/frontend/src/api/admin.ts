import { fetchClient } from './client';
import {
  PlatformStats,
  AdminOrganization,
  AdminPlanOverridePayload,
  FeatureFlag,
  FeatureFlagCreatePayload,
  FeatureFlagUpdatePayload
} from '../types/admin';

export const adminApi = {
  getAnalytics: (): Promise<PlatformStats> =>
    fetchClient('/admin/analytics'),

  listOrganizations: (): Promise<AdminOrganization[]> =>
    fetchClient('/admin/organizations'),

  overridePlan: (orgId: number, data: AdminPlanOverridePayload): Promise<AdminOrganization> =>
    fetchClient(`/admin/organizations/${orgId}/tier`, {
      method: 'PUT',
      body: JSON.stringify(data)
    }),

  listFeatureFlags: (): Promise<FeatureFlag[]> =>
    fetchClient('/admin/feature-flags'),

  createFeatureFlag: (data: FeatureFlagCreatePayload): Promise<FeatureFlag> =>
    fetchClient('/admin/feature-flags', {
      method: 'POST',
      body: JSON.stringify(data)
    }),

  updateFeatureFlag: (flagId: number, data: FeatureFlagUpdatePayload): Promise<FeatureFlag> =>
    fetchClient(`/admin/feature-flags/${flagId}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    }),

  deleteFeatureFlag: (flagId: number): Promise<void> =>
    fetchClient(`/admin/feature-flags/${flagId}`, {
      method: 'DELETE'
    }),

  evaluateClientFlags: (): Promise<Record<string, boolean>> =>
    fetchClient('/admin/feature-flags/eval')
};

