import React, { useState, useEffect } from 'react';
import { 
  Users, Key, Shield, Activity, Plus, Trash2, Copy, Check, 
  RefreshCw, AlertCircle, CheckCircle2, ShieldCheck, Lock, Loader2,
  CreditCard, Clock, ArrowUpRight, CheckCircle, Zap
} from 'lucide-react';
import { useAuthStore } from '../stores/authStore';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import { organizationApi } from '../api/organization';
import { billingApi } from '../api/billing';
import { 
  Member, 
  UserRole, 
  AuditLogEntry, 
  ApiToken, 
  CreatedApiToken, 
  OrganizationDetails 
} from '../types/organization';
import { Plan, Subscription, PlanTier } from '../types/billing';

type TabKey = 'profile' | 'team' | 'tokens' | 'audit' | 'billing';

const ROLE_COLORS: Record<string, string> = {
  owner: 'bg-purple-900/40 text-purple-300 border-purple-600/50',
  admin: 'bg-blue-900/40 text-blue-300 border-blue-600/50',
  engineer: 'bg-emerald-900/40 text-emerald-300 border-emerald-600/50',
  viewer: 'bg-gray-800 text-gray-300 border-gray-600/50',
};

const PLAN_BADGES: Record<string, string> = {
  community: 'bg-gray-800 text-gray-300 border-gray-600',
  team: 'bg-emerald-900/50 text-emerald-300 border-emerald-500',
  enterprise: 'bg-purple-900/50 text-purple-300 border-purple-500',
};

const SettingsPage: React.FC = () => {
  const user = useAuthStore(state => state.user);
  const [activeTab, setActiveTab] = useState<TabKey>('profile');

  // Organization Data
  const [orgDetails, setOrgDetails] = useState<OrganizationDetails | null>(null);
  const [members, setMembers] = useState<Member[]>([]);
  const [tokens, setTokens] = useState<ApiToken[]>([]);
  const [auditLogs, setAuditLogs] = useState<AuditLogEntry[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Billing Data
  const [subscription, setSubscription] = useState<Subscription | null>(null);
  const [plans, setPlans] = useState<Plan[]>([]);
  const [billingCycle, setBillingCycle] = useState<'monthly' | 'annual'>('monthly');
  const [checkoutLoading, setCheckoutLoading] = useState(false);

  // Invite Modal
  const [isInviteOpen, setIsInviteOpen] = useState(false);
  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteName, setInviteName] = useState('');
  const [inviteRole, setInviteRole] = useState<UserRole>('engineer');
  const [invitePassword, setInvitePassword] = useState('');
  const [inviteLoading, setInviteLoading] = useState(false);

  // Token Modal
  const [isTokenModalOpen, setIsTokenModalOpen] = useState(false);
  const [tokenName, setTokenName] = useState('');
  const [tokenScopes, setTokenScopes] = useState<string[]>(['read', 'write']);
  const [tokenExpiresDays, setTokenExpiresDays] = useState(30);
  const [createdTokenResult, setCreatedTokenResult] = useState<CreatedApiToken | null>(null);
  const [copiedToken, setCopiedToken] = useState(false);
  const [tokenLoading, setTokenLoading] = useState(false);

  // Audit filter
  const [auditActionFilter, setAuditActionFilter] = useState('');

  const isOwner = user?.role === 'owner';
  const isOwnerOrAdmin = user?.role === 'owner' || user?.role === 'admin';

  useEffect(() => {
    fetchOrgMetadata();
  }, []);

  useEffect(() => {
    if (activeTab === 'team') {
      loadMembers();
    } else if (activeTab === 'tokens') {
      loadTokens();
    } else if (activeTab === 'audit') {
      loadAuditLogs();
    } else if (activeTab === 'billing') {
      loadBillingData();
    }
  }, [activeTab]);

  const fetchOrgMetadata = async () => {
    try {
      const details = await organizationApi.getDetails();
      setOrgDetails(details);
    } catch {
      // Ignored if user has no org
    }
  };

  const loadMembers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await organizationApi.listMembers();
      setMembers(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load organization members');
    } finally {
      setLoading(false);
    }
  };

  const loadTokens = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await organizationApi.listApiTokens();
      setTokens(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load API tokens');
    } finally {
      setLoading(false);
    }
  };

  const loadAuditLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await organizationApi.listAuditLogs(100, 0, auditActionFilter || undefined);
      setAuditLogs(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load audit logs');
    } finally {
      setLoading(false);
    }
  };

  const loadBillingData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [subData, plansData] = await Promise.all([
        billingApi.getSubscription(),
        billingApi.listPlans()
      ]);
      setSubscription(subData);
      setPlans(plansData);
    } catch (err: any) {
      setError(err?.message || 'Failed to load subscription & billing data');
    } finally {
      setLoading(false);
    }
  };

  const handleInvite = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inviteEmail.trim() || !inviteName.trim()) return;
    setInviteLoading(true);
    setError(null);
    try {
      await organizationApi.inviteMember({
        email: inviteEmail.trim(),
        full_name: inviteName.trim(),
        role: inviteRole,
        password: invitePassword.trim() || undefined
      });
      setSuccessMsg(`Successfully invited ${inviteEmail} as ${inviteRole}`);
      setIsInviteOpen(false);
      setInviteEmail('');
      setInviteName('');
      setInvitePassword('');
      loadMembers();
    } catch (err: any) {
      setError(err?.message || 'Failed to invite member');
    } finally {
      setInviteLoading(false);
    }
  };

  const handleRoleChange = async (userId: number, newRole: UserRole) => {
    setError(null);
    try {
      await organizationApi.updateMemberRole(userId, newRole);
      setSuccessMsg('Member role updated successfully');
      loadMembers();
    } catch (err: any) {
      setError(err?.message || 'Failed to update member role');
    }
  };

  const handleRemoveMember = async (userId: number, name: string) => {
    if (!confirm(`Are you sure you want to remove ${name} from this organization?`)) return;
    setError(null);
    try {
      await organizationApi.removeMember(userId);
      setSuccessMsg('Member removed from organization');
      loadMembers();
    } catch (err: any) {
      setError(err?.message || 'Failed to remove member');
    }
  };

  const handleCreateToken = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!tokenName.trim()) return;
    setTokenLoading(true);
    setError(null);
    try {
      const res = await organizationApi.createApiToken({
        name: tokenName.trim(),
        scopes: tokenScopes,
        expires_days: tokenExpiresDays
      });
      setCreatedTokenResult(res);
      setTokenName('');
      loadTokens();
    } catch (err: any) {
      setError(err?.message || 'Failed to generate API token');
    } finally {
      setTokenLoading(false);
    }
  };

  const handleRevokeToken = async (tokenId: number, name: string) => {
    if (!confirm(`Revoke API token "${name}"? Any external clients using it will immediately lose access.`)) return;
    setError(null);
    try {
      await organizationApi.revokeApiToken(tokenId);
      setSuccessMsg(`Token "${name}" has been revoked.`);
      loadTokens();
    } catch (err: any) {
      setError(err?.message || 'Failed to revoke token');
    }
  };

  const handleUpgradePlan = async (tier: PlanTier) => {
    setCheckoutLoading(true);
    setError(null);
    try {
      const res = await billingApi.createCheckout({
        tier,
        billing_cycle: billingCycle,
        success_url: window.location.origin + '/settings?billing_success=true',
        cancel_url: window.location.origin + '/settings?billing_cancel=true'
      });
      // Redirect to Stripe checkout URL (or simulated success URL)
      if (res.checkout_url) {
        window.location.href = res.checkout_url;
      }
    } catch (err: any) {
      setError(err?.message || 'Failed to create checkout session');
      setCheckoutLoading(false);
    }
  };

  const handleManageBilling = async () => {
    try {
      const res = await billingApi.createPortal(window.location.origin + '/settings');
      if (res.portal_url) {
        window.location.href = res.portal_url;
      }
    } catch (err: any) {
      setError(err?.message || 'Failed to open customer billing portal');
    }
  };

  const handleActivateTrial = async () => {
    setError(null);
    try {
      const res = await billingApi.activateTrial();
      setSuccessMsg(res.message);
      loadBillingData();
    } catch (err: any) {
      setError(err?.message || 'Failed to activate trial');
    }
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedToken(true);
    setTimeout(() => setCopiedToken(false), 2000);
  };

  return (
    <div className="p-8 max-w-6xl mx-auto text-white">
      {/* Header */}
      <div className="flex items-center justify-between mb-8 pb-4 border-b border-[#0f3460]">
        <div>
          <h1 className="text-3xl font-bold flex items-center gap-3">
            <ShieldCheck className="w-8 h-8 text-[#16c79a]" />
            Enterprise Organization & Security Settings
          </h1>
          <p className="text-gray-400 text-sm mt-1">
            Manage multi-tenant access control, team memberships, commercial subscriptions, scoped automation tokens, and audit compliance.
          </p>
        </div>
        {orgDetails && (
          <div className="flex items-center gap-3 bg-[#1a1a2e] border border-[#0f3460] px-4 py-2 rounded-lg text-right">
            <div>
              <span className="text-xs text-gray-400 block">Current Tenant</span>
              <span className="text-sm font-semibold text-white">{orgDetails.name}</span>
            </div>
            <span className="text-xs px-2 py-0.5 rounded bg-[#0f3460] text-emerald-400 font-mono">
              {orgDetails.slug.substring(0, 8)}...
            </span>
          </div>
        )}
      </div>

      {/* Notifications */}
      {error && (
        <div className="mb-6 p-4 rounded-lg bg-red-950/60 border border-red-500/50 flex items-center gap-3 text-red-200 text-sm">
          <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-400" />
          <span>{error}</span>
          <button onClick={() => setError(null)} className="ml-auto text-xs underline text-red-300">Dismiss</button>
        </div>
      )}
      {successMsg && (
        <div className="mb-6 p-4 rounded-lg bg-emerald-950/60 border border-emerald-500/50 flex items-center gap-3 text-emerald-200 text-sm">
          <CheckCircle2 className="w-5 h-5 flex-shrink-0 text-emerald-400" />
          <span>{successMsg}</span>
          <button onClick={() => setSuccessMsg(null)} className="ml-auto text-xs underline text-emerald-300">Dismiss</button>
        </div>
      )}

      {/* Tab Navigation */}
      <div className="flex border-b border-[#0f3460] mb-8 gap-2">
        <button
          onClick={() => setActiveTab('profile')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'profile'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <Lock className="w-4 h-4" /> Profile & Account
        </button>

        <button
          onClick={() => setActiveTab('team')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'team'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <Users className="w-4 h-4" /> Team Members & RBAC
          {members.length > 0 && (
            <span className="text-xs bg-[#0f3460] text-gray-300 px-2 py-0.5 rounded-full">
              {members.length}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveTab('billing')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'billing'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <CreditCard className="w-4 h-4" /> Subscription & Billing
          {subscription && (
            <span className={`text-xs px-2 py-0.5 rounded-full border uppercase font-mono ${PLAN_BADGES[subscription.plan_tier] || PLAN_BADGES.community}`}>
              {subscription.plan_tier}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveTab('tokens')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'tokens'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <Key className="w-4 h-4" /> Scoped API Tokens
          {tokens.length > 0 && (
            <span className="text-xs bg-[#0f3460] text-gray-300 px-2 py-0.5 rounded-full">
              {tokens.length}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveTab('audit')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'audit'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <Activity className="w-4 h-4" /> Security Audit Log
        </button>

        {loading && (
          <div className="ml-auto flex items-center gap-2 text-xs text-[#16c79a] pr-2">
            <Loader2 className="w-3.5 h-3.5 animate-spin" />
            <span>Syncing...</span>
          </div>
        )}
      </div>

      {/* Tab Content */}

      {/* 1. Profile & Account */}
      {activeTab === 'profile' && (
        <div className="space-y-8 max-w-2xl">
          <section className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl p-6 shadow-lg">
            <h2 className="text-lg font-semibold mb-4 text-white flex items-center gap-2">
              <Lock className="w-5 h-5 text-[#16c79a]" /> Authenticated User Profile
            </h2>
            <div className="space-y-4">
              <Input label="Full Name" defaultValue={user?.full_name || user?.name || ''} disabled />
              <Input label="Email Address" defaultValue={user?.email || ''} disabled />
              <div className="flex items-center justify-between p-3 rounded-lg bg-[#16213e] border border-[#0f3460]">
                <div>
                  <span className="text-xs text-gray-400 block">Assigned Role</span>
                  <span className={`text-xs px-2.5 py-1 rounded border font-semibold uppercase tracking-wider inline-block mt-1 ${ROLE_COLORS[user?.role || 'viewer'] || ROLE_COLORS.viewer}`}>
                    {user?.role || 'Viewer'}
                  </span>
                </div>
                <div className="text-right">
                  <span className="text-xs text-gray-400 block">Permissions Level</span>
                  <span className="text-xs text-emerald-400 font-mono">
                    {user?.role === 'owner' ? 'All Privileges (Owner)' : user?.role === 'admin' ? 'Administrative Privileges' : user?.role === 'engineer' ? 'Read / Write / Synthesize' : 'Read-Only'}
                  </span>
                </div>
              </div>
            </div>
          </section>

          {orgDetails && (
            <section className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl p-6 shadow-lg">
              <h2 className="text-lg font-semibold mb-4 text-white flex items-center gap-2">
                <Shield className="w-5 h-5 text-[#16c79a]" /> Organization Metadata
              </h2>
              <div className="grid grid-cols-2 gap-4">
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <span className="text-xs text-gray-400">Organization Name</span>
                  <p className="text-base font-semibold mt-1 text-white">{orgDetails.name}</p>
                </div>
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <span className="text-xs text-gray-400">Total Projects</span>
                  <p className="text-base font-semibold mt-1 text-emerald-400">{orgDetails.projects_count}</p>
                </div>
              </div>
            </section>
          )}
        </div>
      )}

      {/* 2. Team Members & RBAC */}
      {activeTab === 'team' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-white">Team Members & Access Roles</h2>
              <p className="text-gray-400 text-sm mt-0.5">
                Manage members and RBAC hierarchies (OWNER &gt; ADMIN &gt; ENGINEER &gt; VIEWER).
              </p>
            </div>
            {isOwnerOrAdmin && (
              <Button onClick={() => setIsInviteOpen(true)} className="flex items-center gap-2">
                <Plus className="w-4 h-4" /> Invite Member
              </Button>
            )}
          </div>

          <div className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl overflow-hidden shadow-lg">
            <table className="w-full text-left text-sm">
              <thead className="bg-[#16213e] text-gray-400 uppercase text-xs tracking-wider border-b border-[#0f3460]">
                <tr>
                  <th className="px-6 py-4">Member</th>
                  <th className="px-6 py-4">Role</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4">Joined</th>
                  {isOwnerOrAdmin && <th className="px-6 py-4 text-right">Actions</th>}
                </tr>
              </thead>
              <tbody className="divide-y divide-[#0f3460]/60">
                {members.map((m: Member) => (
                  <tr key={m.id} className="hover:bg-[#16213e]/40 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-medium text-white">{m.full_name || 'Anonymous User'}</div>
                      <div className="text-xs text-gray-400 font-mono">{m.email}</div>
                    </td>
                    <td className="px-6 py-4">
                      {isOwnerOrAdmin && m.id !== user?.id ? (
                        <select
                          value={m.role.toLowerCase()}
                          onChange={(e) => handleRoleChange(m.id, e.target.value as UserRole)}
                          className={`text-xs px-2 py-1 rounded border font-semibold uppercase bg-[#16213e] cursor-pointer focus:ring-1 focus:ring-[#16c79a] ${ROLE_COLORS[m.role.toLowerCase()] || ROLE_COLORS.viewer}`}
                        >
                          {user?.role === 'owner' && <option value="owner">OWNER</option>}
                          {user?.role === 'owner' && <option value="admin">ADMIN</option>}
                          <option value="engineer">ENGINEER</option>
                          <option value="viewer">VIEWER</option>
                        </select>
                      ) : (
                        <span className={`text-xs px-2.5 py-1 rounded border font-semibold uppercase tracking-wider ${ROLE_COLORS[m.role.toLowerCase()] || ROLE_COLORS.viewer}`}>
                          {m.role}
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4">
                      <span className={`text-xs px-2 py-0.5 rounded-full ${m.is_active ? 'bg-emerald-950 text-emerald-400 border border-emerald-800' : 'bg-red-950 text-red-400 border border-red-800'}`}>
                        {m.is_active ? 'Active' : 'Deactivated'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-gray-400 text-xs">
                      {m.created_at ? new Date(m.created_at).toLocaleDateString() : '—'}
                    </td>
                    {isOwnerOrAdmin && (
                      <td className="px-6 py-4 text-right">
                        {m.id !== user?.id && (
                          <button
                            onClick={() => handleRemoveMember(m.id, m.full_name || m.email)}
                            className="p-1.5 text-gray-400 hover:text-red-400 hover:bg-red-950/50 rounded transition-colors"
                            title="Remove Member"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* 3. Subscription & Billing */}
      {activeTab === 'billing' && (
        <div className="space-y-8">
          {/* Trial Banner */}
          {subscription?.is_trial ? (
            <div className="p-4 rounded-xl bg-gradient-to-r from-emerald-950/80 to-[#0f3460]/80 border border-emerald-500/60 flex items-center justify-between shadow-lg">
              <div className="flex items-center gap-3">
                <Clock className="w-6 h-6 text-emerald-400 flex-shrink-0" />
                <div>
                  <h3 className="text-sm font-semibold text-white">
                    14-Day Professional Team Trial Active: {subscription.trial_days_remaining} Days Remaining
                  </h3>
                  <p className="text-xs text-gray-300 mt-0.5">
                    Your organization is currently enjoying full Team capabilities: 25 projects, 10 seats, 1M AI tokens, and change impact blast radius analytics.
                  </p>
                </div>
              </div>
              {isOwner && (
                <Button size="sm" onClick={() => handleUpgradePlan('team')} isLoading={checkoutLoading} className="flex-shrink-0">
                  Keep Team Tier
                </Button>
              )}
            </div>
          ) : (
            subscription?.plan_tier === 'community' && isOwner && (
              <div className="p-4 rounded-xl bg-[#16213e] border border-[#0f3460] flex items-center justify-between shadow-lg">
                <div className="flex items-center gap-3">
                  <Zap className="w-6 h-6 text-[#16c79a] flex-shrink-0" />
                  <div>
                    <h3 className="text-sm font-semibold text-white">
                      Experience KIDE Enterprise Professional
                    </h3>
                    <p className="text-xs text-gray-300 mt-0.5">
                      Unlock 25 projects, 10 team seats, ROS2/IEC61499 generators, and BFS blast-radius analysis.
                    </p>
                  </div>
                </div>
                <Button size="sm" onClick={handleActivateTrial} isLoading={checkoutLoading} className="flex-shrink-0">
                  Activate 14-Day Free Trial
                </Button>
              </div>
            )
          )}

          {/* Usage Meters */}
          {subscription && (
            <div className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl p-6 shadow-lg">
              <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                <Zap className="w-5 h-5 text-[#16c79a]" /> Monthly Resource Consumption & Quotas
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Projects Meter */}
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <div className="flex justify-between text-xs mb-2">
                    <span className="text-gray-400">Projects Created</span>
                    <span className="text-white font-mono font-semibold">
                      {subscription.usage.projects_count} / {subscription.usage.max_projects === -1 ? 'Unlimited' : subscription.usage.max_projects}
                    </span>
                  </div>
                  <div className="w-full h-2 rounded-full bg-[#1a1a2e] overflow-hidden">
                    <div 
                      className="h-full bg-[#16c79a] transition-all"
                      style={{
                        width: subscription.usage.max_projects === -1 
                          ? '10%' 
                          : `${Math.min(100, (subscription.usage.projects_count / subscription.usage.max_projects) * 100)}%`
                      }}
                    />
                  </div>
                </div>

                {/* Seats Meter */}
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <div className="flex justify-between text-xs mb-2">
                    <span className="text-gray-400">Team Seats Assigned</span>
                    <span className="text-white font-mono font-semibold">
                      {subscription.usage.members_count} / {subscription.usage.max_members === -1 ? 'Unlimited' : subscription.usage.max_members}
                    </span>
                  </div>
                  <div className="w-full h-2 rounded-full bg-[#1a1a2e] overflow-hidden">
                    <div 
                      className="h-full bg-blue-400 transition-all"
                      style={{
                        width: subscription.usage.max_members === -1 
                          ? '10%' 
                          : `${Math.min(100, (subscription.usage.members_count / subscription.usage.max_members) * 100)}%`
                      }}
                    />
                  </div>
                </div>

                {/* AI Tokens Meter */}
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <div className="flex justify-between text-xs mb-2">
                    <span className="text-gray-400">Monthly AI Tokens</span>
                    <span className="text-white font-mono font-semibold">
                      {subscription.usage.ai_tokens_used.toLocaleString()} / {subscription.usage.monthly_ai_tokens === -1 ? 'Unlimited' : subscription.usage.monthly_ai_tokens.toLocaleString()}
                    </span>
                  </div>
                  <div className="w-full h-2 rounded-full bg-[#1a1a2e] overflow-hidden">
                    <div 
                      className="h-full bg-purple-400 transition-all"
                      style={{
                        width: subscription.usage.monthly_ai_tokens === -1 
                          ? '10%' 
                          : `${Math.min(100, (subscription.usage.ai_tokens_used / subscription.usage.monthly_ai_tokens) * 100)}%`
                      }}
                    />
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Pricing Tier Selector */}
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-xl font-bold text-white">Commercial Subscription Plans</h3>
                <p className="text-gray-400 text-sm mt-0.5">
                  Select the plan that fits your engineering team's scale, simulation volume, and generator needs.
                </p>
              </div>

              {/* Billing Cycle Toggle */}
              <div className="flex items-center gap-2 bg-[#1a1a2e] p-1 border border-[#0f3460] rounded-lg text-xs">
                <button
                  onClick={() => setBillingCycle('monthly')}
                  className={`px-3 py-1.5 rounded transition-colors ${billingCycle === 'monthly' ? 'bg-[#0f3460] text-white font-semibold' : 'text-gray-400 hover:text-white'}`}
                >
                  Monthly
                </button>
                <button
                  onClick={() => setBillingCycle('annual')}
                  className={`px-3 py-1.5 rounded transition-colors flex items-center gap-1 ${billingCycle === 'annual' ? 'bg-[#16c79a] text-black font-semibold' : 'text-gray-400 hover:text-white'}`}
                >
                  Annual <span className="text-[10px] bg-emerald-950 text-emerald-300 px-1 rounded border border-emerald-700">Save 20%</span>
                </button>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {plans.map((p: Plan) => {
                const isCurrent = subscription?.plan_tier === p.tier;
                const priceCents = billingCycle === 'annual' ? p.price_annual_cents : p.price_monthly_cents;
                const displayPrice = priceCents === 0 ? '$0' : `$${(priceCents / (billingCycle === 'annual' ? 1200 : 100)).toFixed(0)}`;

                return (
                  <div 
                    key={p.tier} 
                    className={`rounded-xl p-6 flex flex-col justify-between transition-all bg-[#1a1a2e] border ${
                      isCurrent 
                        ? 'border-[#16c79a] ring-2 ring-[#16c79a]/30 shadow-xl' 
                        : 'border-[#0f3460] hover:border-gray-500'
                    }`}
                  >
                    <div>
                      <div className="flex justify-between items-center mb-3">
                        <span className="text-lg font-bold text-white">{p.name}</span>
                        {isCurrent && (
                          <span className="text-[10px] px-2 py-0.5 rounded-full bg-[#16c79a] text-black font-semibold uppercase tracking-wider">
                            Current Plan
                          </span>
                        )}
                      </div>

                      <div className="mb-4">
                        <span className="text-3xl font-extrabold text-white">{displayPrice}</span>
                        <span className="text-gray-400 text-xs ml-1">/ month</span>
                        {billingCycle === 'annual' && priceCents > 0 && (
                          <span className="text-[11px] block text-gray-500 mt-0.5">Billed annually (${(priceCents / 100).toFixed(0)}/yr)</span>
                        )}
                      </div>

                      <p className="text-xs text-gray-400 mb-6 leading-relaxed">
                        {p.description}
                      </p>

                      <div className="space-y-2.5 text-xs text-gray-300 border-t border-[#0f3460] pt-4 mb-6">
                        <div className="flex items-center gap-2">
                          <CheckCircle className="w-4 h-4 text-[#16c79a] flex-shrink-0" />
                          <span>{p.max_projects === -1 ? 'Unlimited' : `${p.max_projects} Active`} Engineering Projects</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <CheckCircle className="w-4 h-4 text-[#16c79a] flex-shrink-0" />
                          <span>{p.max_members === -1 ? 'Unlimited' : `${p.max_members} Team`} Organization Seats</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <CheckCircle className="w-4 h-4 text-[#16c79a] flex-shrink-0" />
                          <span>{p.monthly_ai_tokens === -1 ? 'Unlimited' : `${p.monthly_ai_tokens.toLocaleString()}`} Monthly AI Copilot Tokens</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <CheckCircle className="w-4 h-4 text-[#16c79a] flex-shrink-0" />
                          <span>Generators: <strong className="text-white">{p.allowed_generators.join(', ').toUpperCase()}</strong></span>
                        </div>
                        <div className="flex items-center gap-2">
                          <CheckCircle className="w-4 h-4 text-[#16c79a] flex-shrink-0" />
                          <span>{p.traceability_export ? 'Traceability Matrix & Blast Radius Engine' : 'Basic Model Traceability'}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <CheckCircle className="w-4 h-4 text-[#16c79a] flex-shrink-0" />
                          <span>{p.audit_retention_days}-Day Immutable Security Audit Trail</span>
                        </div>
                      </div>
                    </div>

                    {isOwner && (
                      <div>
                        {isCurrent ? (
                          <Button variant="secondary" className="w-full flex items-center justify-center gap-1.5" onClick={handleManageBilling}>
                            Manage in Billing Portal <ArrowUpRight className="w-4 h-4" />
                          </Button>
                        ) : (
                          <Button 
                            className="w-full"
                            isLoading={checkoutLoading}
                            onClick={() => handleUpgradePlan(p.tier as PlanTier)}
                          >
                            {p.tier === 'community' ? 'Downgrade to Community' : `Upgrade to ${p.name}`}
                          </Button>
                        )}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* 4. Scoped API Tokens */}
      {activeTab === 'tokens' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-white">Scoped Automation API Tokens</h2>
              <p className="text-gray-400 text-sm mt-0.5">
                Generate high-entropy customer API tokens (<code className="text-[#16c79a]">kide_live_...</code>) for CI/CD runners, external CLI tools, and automation pipelines.
              </p>
            </div>
            {isOwnerOrAdmin && (
              <Button onClick={() => { setCreatedTokenResult(null); setIsTokenModalOpen(true); }} className="flex items-center gap-2">
                <Plus className="w-4 h-4" /> Create API Token
              </Button>
            )}
          </div>

          <div className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl overflow-hidden shadow-lg">
            {tokens.length === 0 ? (
              <div className="p-12 text-center text-gray-400">
                <Key className="w-12 h-12 mx-auto text-gray-600 mb-3" />
                <p className="text-base font-medium">No active API tokens found</p>
                <p className="text-xs text-gray-500 mt-1">Generate a scoped token to connect automated engineering pipelines.</p>
              </div>
            ) : (
              <table className="w-full text-left text-sm">
                <thead className="bg-[#16213e] text-gray-400 uppercase text-xs tracking-wider border-b border-[#0f3460]">
                  <tr>
                    <th className="px-6 py-4">Name</th>
                    <th className="px-6 py-4">Prefix</th>
                    <th className="px-6 py-4">Scopes</th>
                    <th className="px-6 py-4">Created</th>
                    <th className="px-6 py-4">Expires</th>
                    <th className="px-6 py-4">Last Used</th>
                    {isOwnerOrAdmin && <th className="px-6 py-4 text-right">Revoke</th>}
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#0f3460]/60">
                  {tokens.map((tok: ApiToken) => (
                    <tr key={tok.id} className="hover:bg-[#16213e]/40 transition-colors">
                      <td className="px-6 py-4 font-medium text-white">{tok.name}</td>
                      <td className="px-6 py-4 font-mono text-xs text-[#16c79a]">{tok.prefix}</td>
                      <td className="px-6 py-4">
                        <div className="flex gap-1.5 flex-wrap">
                          {tok.scopes.split(',').map((s: string) => (
                            <span key={s} className="text-xs px-2 py-0.5 rounded bg-[#0f3460] text-gray-300 font-mono">
                              {s.trim()}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-6 py-4 text-gray-400 text-xs">
                        {tok.created_at ? new Date(tok.created_at).toLocaleDateString() : '—'}
                      </td>
                      <td className="px-6 py-4 text-gray-400 text-xs">
                        {tok.expires_at ? new Date(tok.expires_at).toLocaleDateString() : 'Never'}
                      </td>
                      <td className="px-6 py-4 text-gray-400 text-xs">
                        {tok.last_used_at ? new Date(tok.last_used_at).toLocaleString() : 'Never'}
                      </td>
                      {isOwnerOrAdmin && (
                        <td className="px-6 py-4 text-right">
                          <button
                            onClick={() => handleRevokeToken(tok.id, tok.name)}
                            className="p-1.5 text-gray-400 hover:text-red-400 hover:bg-red-950/50 rounded transition-colors"
                            title="Revoke Token"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}

      {/* 5. Security Audit Log */}
      {activeTab === 'audit' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-white">Security & Provenance Audit Trail</h2>
              <p className="text-gray-400 text-sm mt-0.5">
                Tamper-resistant append-only ledger of all tenant activities, memberships, and model operations.
              </p>
            </div>
            <div className="flex items-center gap-3">
              <select
                value={auditActionFilter}
                onChange={(e) => setAuditActionFilter(e.target.value)}
                className="bg-[#16213e] border border-[#0f3460] rounded-md px-3 py-1.5 text-xs text-white focus:outline-none focus:ring-1 focus:ring-[#16c79a]"
              >
                <option value="">All Audit Actions</option>
                <option value="user:login">user:login</option>
                <option value="member:invite">member:invite</option>
                <option value="member:role_updated">member:role_updated</option>
                <option value="project:create">project:create</option>
                <option value="file:create">file:create</option>
                <option value="file:update">file:update</option>
                <option value="api_token:create">api_token:create</option>
                <option value="api_token:revoke">api_token:revoke</option>
                <option value="billing:subscription_activated">billing:subscription_activated</option>
                <option value="billing:subscription_canceled">billing:subscription_canceled</option>
              </select>
              <Button size="sm" variant="secondary" onClick={loadAuditLogs} className="flex items-center gap-1.5">
                <RefreshCw className="w-3.5 h-3.5" /> Refresh
              </Button>
            </div>
          </div>

          <div className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl overflow-hidden shadow-lg">
            {auditLogs.length === 0 ? (
              <div className="p-12 text-center text-gray-400">
                <Activity className="w-12 h-12 mx-auto text-gray-600 mb-3" />
                <p className="text-base font-medium">No audit entries found</p>
                <p className="text-xs text-gray-500 mt-1">Audit entries are automatically logged as operations occur.</p>
              </div>
            ) : (
              <table className="w-full text-left text-sm">
                <thead className="bg-[#16213e] text-gray-400 uppercase text-xs tracking-wider border-b border-[#0f3460]">
                  <tr>
                    <th className="px-6 py-4">Timestamp</th>
                    <th className="px-6 py-4">Actor</th>
                    <th className="px-6 py-4">Action</th>
                    <th className="px-6 py-4">Target</th>
                    <th className="px-6 py-4">Details</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#0f3460]/60">
                  {auditLogs.map((log: AuditLogEntry) => (
                    <tr key={log.id} className="hover:bg-[#16213e]/40 transition-colors">
                      <td className="px-6 py-4 text-xs font-mono text-gray-400 whitespace-nowrap">
                        {log.created_at ? new Date(log.created_at).toLocaleString() : '—'}
                      </td>
                      <td className="px-6 py-4 font-mono text-xs text-white">
                        {log.actor_email || 'System'}
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-xs px-2.5 py-1 rounded bg-[#0f3460] text-emerald-300 font-mono font-medium">
                          {log.action}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-xs text-gray-300 font-mono">
                        {log.target_type ? `${log.target_type} #${log.target_id || ''}` : '—'}
                      </td>
                      <td className="px-6 py-4 text-xs text-gray-400 max-w-md truncate font-mono">
                        {log.details || '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}

      {/* Invite Member Modal */}
      <Modal isOpen={isInviteOpen} onClose={() => setIsInviteOpen(false)} title="Invite Team Member">
        <form onSubmit={handleInvite} className="space-y-4">
          <Input
            label="Full Name"
            placeholder="Jane Doe"
            value={inviteName}
            onChange={(e) => setInviteName(e.target.value)}
            required
          />
          <Input
            label="Email Address"
            type="email"
            placeholder="jane@enterprise.com"
            value={inviteEmail}
            onChange={(e) => setInviteEmail(e.target.value)}
            required
          />
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Access Role</label>
            <select
              value={inviteRole}
              onChange={(e) => setInviteRole(e.target.value as UserRole)}
              className="w-full h-10 rounded-md border border-[#0f3460] bg-[#1a1a2e] px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#16c79a]"
            >
              {user?.role === 'owner' && <option value="owner">Owner (Full Tenancy Management)</option>}
              {user?.role === 'owner' && <option value="admin">Admin (User & Token Management)</option>}
              <option value="engineer">Engineer (Model Synthesis, Validation, Editing)</option>
              <option value="viewer">Viewer (Read-Only Inspection)</option>
            </select>
          </div>
          <Input
            label="Initial Temporary Password (Optional)"
            type="password"
            placeholder="Leave blank to auto-generate"
            value={invitePassword}
            onChange={(e) => setInvitePassword(e.target.value)}
          />
          <div className="flex justify-end gap-3 pt-4 border-t border-[#0f3460]">
            <Button variant="ghost" type="button" onClick={() => setIsInviteOpen(false)}>Cancel</Button>
            <Button type="submit" isLoading={inviteLoading}>Send Invitation</Button>
          </div>
        </form>
      </Modal>

      {/* Scoped API Token Modal */}
      <Modal isOpen={isTokenModalOpen} onClose={() => setIsTokenModalOpen(false)} title="Generate Scoped API Token">
        {createdTokenResult ? (
          <div className="space-y-4">
            <div className="p-4 rounded-lg bg-emerald-950/60 border border-emerald-500/50 text-sm text-emerald-200">
              <div className="font-semibold flex items-center gap-2 mb-1">
                <CheckCircle2 className="w-5 h-5 text-emerald-400" /> Token Created Successfully
              </div>
              <p className="text-xs text-emerald-300">
                Copy this token now. For security purposes, this secret key will never be shown again!
              </p>
            </div>

            <div className="space-y-1">
              <label className="text-xs text-gray-400 font-mono">Bearer Token Secret</label>
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  readOnly
                  value={createdTokenResult.raw_token}
                  className="flex-1 bg-[#16213e] border border-[#0f3460] rounded px-3 py-2 text-sm font-mono text-emerald-400"
                />
                <Button size="sm" onClick={() => copyToClipboard(createdTokenResult.raw_token)}>
                  {copiedToken ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                </Button>
              </div>
            </div>

            <div className="flex justify-end pt-4 border-t border-[#0f3460]">
              <Button onClick={() => setIsTokenModalOpen(false)}>Done</Button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleCreateToken} className="space-y-4">
            <Input
              label="Token Name / Description"
              placeholder="e.g. GitHub Actions CI Runner"
              value={tokenName}
              onChange={(e) => setTokenName(e.target.value)}
              required
            />

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Token Scopes</label>
              <div className="grid grid-cols-3 gap-2">
                {['read', 'write', 'admin'].map(scope => {
                  const isChecked = tokenScopes.includes(scope);
                  return (
                    <label key={scope} className={`flex items-center gap-2 p-2.5 rounded border text-xs cursor-pointer ${isChecked ? 'bg-[#0f3460] border-[#16c79a] text-white' : 'bg-[#16213e] border-[#0f3460] text-gray-400'}`}>
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => {
                          if (isChecked) {
                            setTokenScopes(tokenScopes.filter((s: string) => s !== scope));
                          } else {
                            setTokenScopes([...tokenScopes, scope]);
                          }
                        }}
                        className="rounded text-[#16c79a]"
                      />
                      <span className="font-mono uppercase">{scope}</span>
                    </label>
                  );
                })}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-1.5">Expiration Period</label>
              <select
                value={tokenExpiresDays}
                onChange={(e) => setTokenExpiresDays(Number(e.target.value))}
                className="w-full h-10 rounded-md border border-[#0f3460] bg-[#1a1a2e] px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#16c79a]"
              >
                <option value={7}>7 Days</option>
                <option value={30}>30 Days (Recommended)</option>
                <option value={90}>90 Days</option>
                <option value={365}>1 Year</option>
                <option value={0}>Never Expire</option>
              </select>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-[#0f3460]">
              <Button variant="ghost" type="button" onClick={() => setIsTokenModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={tokenLoading}>Generate Token</Button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
};

export default SettingsPage;
