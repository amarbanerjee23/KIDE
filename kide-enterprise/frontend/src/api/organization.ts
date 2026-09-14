import { fetchClient } from './client';
import {
  OrganizationDetails,
  Member,
  InviteMemberPayload,
  UserRole,
  AuditLogEntry,
  ApiToken,
  CreateApiTokenPayload,
  CreatedApiToken
} from '../types/organization';

export const organizationApi = {
  getDetails: (): Promise<OrganizationDetails> => 
    fetchClient('/org/details'),

  listMembers: (): Promise<Member[]> => 
    fetchClient('/org/members'),

  inviteMember: (data: InviteMemberPayload): Promise<Member> =>
    fetchClient('/org/members/invite', {
      method: 'POST',
      body: JSON.stringify(data)
    }),

  updateMemberRole: (userId: number, role: UserRole): Promise<Member> =>
    fetchClient(`/org/members/${userId}/role`, {
      method: 'PUT',
      body: JSON.stringify({ role })
    }),

  removeMember: (userId: number): Promise<{ status: string; user_id: number }> =>
    fetchClient(`/org/members/${userId}`, {
      method: 'DELETE'
    }),

  listAuditLogs: (limit: number = 50, offset: number = 0, action?: string): Promise<AuditLogEntry[]> => {
    let url = `/org/audit-logs?limit=${limit}&offset=${offset}`;
    if (action) {
      url += `&action=${encodeURIComponent(action)}`;
    }
    return fetchClient(url);
  },

  createApiToken: (data: CreateApiTokenPayload): Promise<CreatedApiToken> =>
    fetchClient('/org/api-tokens', {
      method: 'POST',
      body: JSON.stringify({
        name: data.name,
        scopes: data.scopes || ['read', 'write'],
        expires_days: data.expires_days ?? 30
      })
    }),

  listApiTokens: (): Promise<ApiToken[]> =>
    fetchClient('/org/api-tokens'),

  revokeApiToken: (tokenId: number): Promise<{ status: string; token_id: number }> =>
    fetchClient(`/org/api-tokens/${tokenId}`, {
      method: 'DELETE'
    })
};

