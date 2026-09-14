export type UserRole = 'owner' | 'admin' | 'engineer' | 'viewer';

export interface OrganizationDetails {
  id: number;
  name: string;
  slug: string;
  created_at?: string;
  members_count: number;
  projects_count: number;
}

export interface Member {
  id: number;
  email: string;
  full_name?: string;
  role: UserRole | string;
  is_active: boolean;
  created_at?: string;
}

export interface InviteMemberPayload {
  email: string;
  full_name: string;
  role: UserRole;
  password?: string;
}

export interface UpdateMemberRolePayload {
  role: UserRole;
}

export interface AuditLogEntry {
  id: number;
  org_id: number;
  actor_id?: number;
  actor_email?: string;
  action: string;
  target_type?: string;
  target_id?: string;
  details?: string;
  ip_address?: string;
  created_at?: string;
}

export interface ApiToken {
  id: number;
  name: string;
  prefix: string;
  scopes: string;
  is_active: boolean;
  expires_at?: string;
  created_at?: string;
  last_used_at?: string;
}

export interface CreateApiTokenPayload {
  name: string;
  scopes?: string[];
  expires_days?: number;
}

export interface CreatedApiToken {
  id: number;
  name: string;
  prefix: string;
  raw_token: string;
  scopes: string;
  expires_at?: string;
  created_at?: string;
}

