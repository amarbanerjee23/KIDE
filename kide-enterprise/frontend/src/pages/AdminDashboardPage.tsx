import React, { useState, useEffect } from 'react';
import {
  DollarSign, TrendingUp, Building2, Cpu, AlertTriangle,
  CheckCircle2, AlertCircle, RefreshCw, Sliders, ToggleLeft, ToggleRight,
  ShieldCheck, Search, Plus, Trash2, Clock, Edit2
} from 'lucide-react';
import { useAuthStore } from '../stores/authStore';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import { adminApi } from '../api/admin';
import {
  PlatformStats,
  AdminOrganization,
  FeatureFlag
} from '../types/admin';

type TabKey = 'overview' | 'organizations' | 'flags';

const TIER_COLORS: Record<string, string> = {
  community: 'bg-gray-800 text-gray-300 border-gray-600/50',
  team: 'bg-blue-900/40 text-blue-300 border-blue-600/50',
  enterprise: 'bg-purple-900/40 text-purple-300 border-purple-600/50'
};

const AdminDashboardPage: React.FC = () => {
  const user = useAuthStore(state => state.user);
  const [activeTab, setActiveTab] = useState<TabKey>('overview');

  const [stats, setStats] = useState<PlatformStats | null>(null);
  const [organizations, setOrganizations] = useState<AdminOrganization[]>([]);
  const [flags, setFlags] = useState<FeatureFlag[]>([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  // Filter & Search
  const [searchQuery, setSearchQuery] = useState('');
  const [tierFilter, setTierFilter] = useState<'all' | 'community' | 'team' | 'enterprise'>('all');

  // Plan Override Modal
  const [isOverrideOpen, setIsOverrideOpen] = useState(false);
  const [selectedOrg, setSelectedOrg] = useState<AdminOrganization | null>(null);
  const [overrideTier, setOverrideTier] = useState<string>('team');
  const [extendDays, setExtendDays] = useState<number>(14);
  const [overrideLoading, setOverrideLoading] = useState(false);

  // New Feature Flag Modal
  const [isNewFlagOpen, setIsNewFlagOpen] = useState(false);
  const [flagKey, setFlagKey] = useState('');
  const [flagName, setFlagName] = useState('');
  const [flagDesc, setFlagDesc] = useState('');
  const [flagMinTier, setFlagMinTier] = useState<string>('community');
  const [flagRollout, setFlagRollout] = useState<number>(100);
  const [flagAllowedOrgs, setFlagAllowedOrgs] = useState<string>('');
  const [flagLoading, setFlagLoading] = useState(false);

  const isOwner = user?.role === 'owner';

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [analyticsData, orgsData, flagsData] = await Promise.all([
        adminApi.getAnalytics(),
        adminApi.listOrganizations(),
        adminApi.listFeatureFlags()
      ]);
      setStats(analyticsData);
      setOrganizations(orgsData);
      setFlags(flagsData);
    } catch (err: any) {
      setError(err?.message || 'Failed to load platform administration metrics');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenOverride = (org: AdminOrganization) => {
    setSelectedOrg(org);
    setOverrideTier(org.plan_tier);
    setExtendDays(14);
    setIsOverrideOpen(true);
  };

  const handleSaveOverride = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedOrg) return;
    setOverrideLoading(true);
    setError(null);
    try {
      await adminApi.overridePlan(selectedOrg.id, {
        plan_tier: overrideTier,
        extend_trial_days: extendDays > 0 ? extendDays : undefined
      });
      setSuccessMsg(`Successfully updated plan tier for ${selectedOrg.name}`);
      setIsOverrideOpen(false);
      loadAllData();
    } catch (err: any) {
      setError(err?.message || 'Failed to update organization plan');
    } finally {
      setOverrideLoading(false);
    }
  };

  const handleToggleFlag = async (flag: FeatureFlag) => {
    setError(null);
    try {
      const updated = await adminApi.updateFeatureFlag(flag.id, {
        is_enabled: !flag.is_enabled
      });
      setFlags(prev => prev.map(f => f.id === flag.id ? updated : f));
      setSuccessMsg(`Feature flag '${flag.key}' is now ${updated.is_enabled ? 'enabled' : 'disabled'}`);
    } catch (err: any) {
      setError(err?.message || 'Failed to toggle feature flag');
    }
  };

  const handleUpdateRollout = async (flagId: number, rollout: number) => {
    try {
      const updated = await adminApi.updateFeatureFlag(flagId, {
        rollout_percentage: rollout
      });
      setFlags(prev => prev.map(f => f.id === flagId ? updated : f));
    } catch (err: any) {
      setError(err?.message || 'Failed to update rollout percentage');
    }
  };

  const handleCreateFlag = async (e: React.FormEvent) => {
    e.preventDefault();
    setFlagLoading(true);
    setError(null);
    try {
      const orgIds = flagAllowedOrgs
        .split(',')
        .map(s => parseInt(s.trim(), 10))
        .filter(n => !isNaN(n));

      await adminApi.createFeatureFlag({
        key: flagKey.trim(),
        name: flagName.trim(),
        description: flagDesc.trim() || undefined,
        minimum_tier: flagMinTier,
        rollout_percentage: flagRollout,
        allowed_org_ids: orgIds
      });

      setSuccessMsg(`Created feature flag '${flagKey}'`);
      setIsNewFlagOpen(false);
      setFlagKey('');
      setFlagName('');
      setFlagDesc('');
      loadAllData();
    } catch (err: any) {
      setError(err?.message || 'Failed to create feature flag');
    } finally {
      setFlagLoading(false);
    }
  };

  const handleDeleteFlag = async (flagId: number, flagKey: string) => {
    if (!confirm(`Are you sure you want to delete feature flag '${flagKey}'?`)) return;
    try {
      await adminApi.deleteFeatureFlag(flagId);
      setFlags(prev => prev.filter(f => f.id !== flagId));
      setSuccessMsg(`Deleted feature flag '${flagKey}'`);
    } catch (err: any) {
      setError(err?.message || 'Failed to delete feature flag');
    }
  };

  // Filtered organizations
  const filteredOrgs = organizations.filter(org => {
    const matchesSearch =
      org.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      org.slug.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (org.owner_email && org.owner_email.toLowerCase().includes(searchQuery.toLowerCase()));
    const matchesTier = tierFilter === 'all' || org.plan_tier === tierFilter;
    return matchesSearch && matchesTier;
  });

  if (!isOwner) {
    return (
      <div className="p-12 max-w-2xl mx-auto text-center text-white">
        <AlertTriangle className="w-16 h-16 text-red-400 mx-auto mb-4" />
        <h2 className="text-2xl font-bold mb-2">Access Restricted</h2>
        <p className="text-gray-400 text-sm">
          The Owner Business Intelligence & Administrative Console is reserved exclusively for platform organization owners.
        </p>
      </div>
    );
  }

  return (
    <div className="p-8 max-w-7xl mx-auto text-white">
      {/* Header */}
      <div className="flex items-center justify-between mb-8 pb-4 border-b border-[#0f3460]">
        <div>
          <h1 className="text-3xl font-bold flex items-center gap-3">
            <ShieldCheck className="w-8 h-8 text-[#16c79a]" />
            Owner Business Intelligence & Platform Console
          </h1>
          <p className="text-gray-400 text-sm mt-1">
            Real-time SaaS financial metrics, multi-tenant customer governance, and dynamic progressive feature rollouts.
          </p>
        </div>
        <Button variant="secondary" onClick={loadAllData} isLoading={loading} className="flex items-center gap-2">
          <RefreshCw className="w-4 h-4" /> Refresh Platform Metrics
        </Button>
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

      {/* Top Financial & Platform KPI Stat Cards */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-5 mb-8">
          {/* Card 1: MRR */}
          <div className="p-5 rounded-xl bg-[#1a1a2e] border border-[#0f3460] shadow-lg">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Monthly Recurring</span>
              <DollarSign className="w-5 h-5 text-[#16c79a]" />
            </div>
            <div className="text-2xl font-bold text-white">${stats.mrr_usd.toLocaleString()}</div>
            <div className="text-xs text-gray-400 mt-1 flex items-center gap-1">
              <span>ARR Run-rate:</span>
              <span className="text-emerald-400 font-mono font-medium">${stats.arr_usd.toLocaleString()}</span>
            </div>
          </div>

          {/* Card 2: Paying Subscribers */}
          <div className="p-5 rounded-xl bg-[#1a1a2e] border border-[#0f3460] shadow-lg">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Paid Subscribers</span>
              <TrendingUp className="w-5 h-5 text-blue-400" />
            </div>
            <div className="text-2xl font-bold text-white">{stats.active_subscribers}</div>
            <div className="text-xs text-gray-400 mt-1">
              Conversion rate: <strong className="text-white">{stats.conversion_rate_pct}%</strong>
            </div>
          </div>

          {/* Card 3: Free Trials */}
          <div className="p-5 rounded-xl bg-[#1a1a2e] border border-[#0f3460] shadow-lg">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Active Trials</span>
              <Clock className="w-5 h-5 text-purple-400" />
            </div>
            <div className="text-2xl font-bold text-white">{stats.active_trials}</div>
            <div className="text-xs text-gray-400 mt-1">
              {stats.trials_expiring_soon > 0 ? (
                <span className="text-amber-400 font-medium">⚠️ {stats.trials_expiring_soon} expiring soon</span>
              ) : (
                <span>All trials healthy</span>
              )}
            </div>
          </div>

          {/* Card 4: AI Copilot Consumption */}
          <div className="p-5 rounded-xl bg-[#1a1a2e] border border-[#0f3460] shadow-lg">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">AI Copilot Tokens</span>
              <Cpu className="w-5 h-5 text-teal-400" />
            </div>
            <div className="text-2xl font-bold text-white">{stats.total_ai_tokens_used.toLocaleString()}</div>
            <div className="text-xs text-gray-400 mt-1">Platform aggregate</div>
          </div>

          {/* Card 5: Churn & Health */}
          <div className="p-5 rounded-xl bg-[#1a1a2e] border border-[#0f3460] shadow-lg">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Gross Churn</span>
              <Building2 className="w-5 h-5 text-gray-400" />
            </div>
            <div className="text-2xl font-bold text-white">{stats.churn_rate_pct}%</div>
            <div className="text-xs text-gray-400 mt-1">
              {stats.total_organizations} total organizations
            </div>
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="flex border-b border-[#0f3460] mb-8 gap-2">
        <button
          onClick={() => setActiveTab('overview')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'overview'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <TrendingUp className="w-4 h-4" /> Revenue & Resource Breakdown
        </button>

        <button
          onClick={() => setActiveTab('organizations')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'organizations'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <Building2 className="w-4 h-4" /> Organization Directory ({organizations.length})
        </button>

        <button
          onClick={() => setActiveTab('flags')}
          className={`px-5 py-3 text-sm font-medium border-b-2 flex items-center gap-2 transition-colors ${
            activeTab === 'flags'
              ? 'border-[#16c79a] text-[#16c79a]'
              : 'border-transparent text-gray-400 hover:text-white'
          }`}
        >
          <Sliders className="w-4 h-4" /> Feature Flags Console ({flags.length})
        </button>
      </div>

      {/* Tab 1: Executive Overview */}
      {activeTab === 'overview' && stats && (
        <div className="space-y-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Plan Tier Distribution */}
            <div className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl p-6 shadow-lg">
              <h3 className="text-lg font-bold text-white mb-4">Subscription Tier Distribution</h3>
              <div className="space-y-4">
                {['community', 'team', 'enterprise'].map(tier => {
                  const count = stats.tier_distribution[tier] || 0;
                  const total = Math.max(1, stats.total_organizations);
                  const pct = Math.round((count / total) * 100);
                  return (
                    <div key={tier} className="space-y-1.5">
                      <div className="flex justify-between text-xs">
                        <span className="font-semibold uppercase tracking-wider text-gray-300">{tier}</span>
                        <span className="text-gray-400 font-mono">{count} orgs ({pct}%)</span>
                      </div>
                      <div className="w-full h-3 rounded-full bg-[#16213e] overflow-hidden">
                        <div
                          className={`h-full transition-all ${
                            tier === 'enterprise'
                              ? 'bg-purple-500'
                              : tier === 'team'
                              ? 'bg-blue-500'
                              : 'bg-gray-500'
                          }`}
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Platform Resource Utilization */}
            <div className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl p-6 shadow-lg">
              <h3 className="text-lg font-bold text-white mb-4">Platform Infrastructure Saturation</h3>
              <div className="grid grid-cols-2 gap-4">
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <div className="text-xs text-gray-400 mb-1">Total Active Projects</div>
                  <div className="text-2xl font-bold text-white">{stats.total_projects}</div>
                </div>
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <div className="text-xs text-gray-400 mb-1">Total Industrial Users</div>
                  <div className="text-2xl font-bold text-white">{stats.total_users}</div>
                </div>
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <div className="text-xs text-gray-400 mb-1">Avg Seats Per Org</div>
                  <div className="text-2xl font-bold text-white">
                    {(stats.total_users / Math.max(1, stats.total_organizations)).toFixed(1)}
                  </div>
                </div>
                <div className="p-4 rounded-lg bg-[#16213e] border border-[#0f3460]">
                  <div className="text-xs text-gray-400 mb-1">Avg Projects Per Org</div>
                  <div className="text-2xl font-bold text-white">
                    {(stats.total_projects / Math.max(1, stats.total_organizations)).toFixed(1)}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 2: Organization Directory */}
      {activeTab === 'organizations' && (
        <div className="space-y-6">
          {/* Filters & Search */}
          <div className="flex flex-wrap items-center justify-between gap-4">
            <div className="relative w-80">
              <Search className="w-4 h-4 text-gray-400 absolute left-3 top-3" />
              <input
                type="text"
                placeholder="Search organizations or owners..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full h-10 pl-9 pr-4 rounded-md border border-[#0f3460] bg-[#1a1a2e] text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#16c79a]"
              />
            </div>

            <div className="flex items-center gap-2">
              {(['all', 'community', 'team', 'enterprise'] as const).map(tier => (
                <button
                  key={tier}
                  onClick={() => setTierFilter(tier)}
                  className={`px-3 py-1.5 rounded-md text-xs font-semibold uppercase tracking-wider transition-colors ${
                    tierFilter === tier
                      ? 'bg-[#16c79a] text-black'
                      : 'bg-[#1a1a2e] text-gray-400 border border-[#0f3460] hover:text-white'
                  }`}
                >
                  {tier}
                </button>
              ))}
            </div>
          </div>

          {/* Directory Table */}
          <div className="bg-[#1a1a2e] border border-[#0f3460] rounded-xl overflow-hidden shadow-lg">
            <table className="w-full text-left text-sm">
              <thead className="bg-[#16213e] text-xs font-semibold text-gray-400 uppercase tracking-wider border-b border-[#0f3460]">
                <tr>
                  <th className="px-6 py-4">Organization</th>
                  <th className="px-6 py-4">Owner Contact</th>
                  <th className="px-6 py-4">Commercial Tier</th>
                  <th className="px-6 py-4">Status & Trial</th>
                  <th className="px-6 py-4">Projects</th>
                  <th className="px-6 py-4">Seats</th>
                  <th className="px-6 py-4">AI Tokens</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#0f3460]/60">
                {filteredOrgs.map(org => (
                  <tr key={org.id} className="hover:bg-[#16213e]/40 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-semibold text-white">{org.name}</div>
                      <div className="text-xs text-gray-500 font-mono">{org.slug.substring(0, 8)}...</div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="text-xs text-white">{org.owner_name || '—'}</div>
                      <div className="text-xs text-gray-400 font-mono">{org.owner_email || '—'}</div>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`text-xs px-2.5 py-1 rounded border font-semibold uppercase ${TIER_COLORS[org.plan_tier] || TIER_COLORS.community}`}>
                        {org.plan_tier}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-xs">
                      {org.is_trial ? (
                        <span className="text-amber-400 font-medium flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5" /> {org.trial_days_remaining}d trial
                        </span>
                      ) : (
                        <span className="text-emerald-400 capitalize">{org.subscription_status}</span>
                      )}
                    </td>
                    <td className="px-6 py-4 font-mono text-xs text-white">{org.projects_count}</td>
                    <td className="px-6 py-4 font-mono text-xs text-white">{org.members_count}</td>
                    <td className="px-6 py-4 font-mono text-xs text-gray-300">{org.ai_tokens_used.toLocaleString()}</td>
                    <td className="px-6 py-4 text-right">
                      <Button size="sm" variant="secondary" onClick={() => handleOpenOverride(org)}>
                        <Edit2 className="w-3.5 h-3.5 mr-1" /> Override Plan
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 3: Feature Flags Console */}
      {activeTab === 'flags' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-white">Dynamic Feature Flags & Progressive Rollout</h2>
              <p className="text-gray-400 text-xs mt-1">
                Configure runtime feature gates, tier prerequisites, target whitelists, and gradual percentage rollouts.
              </p>
            </div>
            <Button onClick={() => setIsNewFlagOpen(true)} className="flex items-center gap-1.5">
              <Plus className="w-4 h-4" /> Create Feature Flag
            </Button>
          </div>

          <div className="space-y-4">
            {flags.map(flag => (
              <div key={flag.id} className="p-5 rounded-xl bg-[#1a1a2e] border border-[#0f3460] shadow-md flex items-center justify-between">
                <div className="space-y-1.5 max-w-xl">
                  <div className="flex items-center gap-3">
                    <span className="text-base font-bold text-white">{flag.name}</span>
                    <span className="text-xs px-2 py-0.5 rounded bg-[#0f3460] text-emerald-300 font-mono">
                      {flag.key}
                    </span>
                    <span className={`text-[10px] px-2 py-0.5 rounded border uppercase font-semibold ${TIER_COLORS[flag.minimum_tier]}`}>
                      Min: {flag.minimum_tier}
                    </span>
                  </div>
                  <p className="text-xs text-gray-400 leading-relaxed">
                    {flag.description || 'No description provided.'}
                  </p>
                  {flag.allowed_org_ids.length > 0 && (
                    <div className="text-[11px] text-gray-400 flex items-center gap-1">
                      <span>Whitelisted Org IDs:</span>
                      <span className="text-emerald-400 font-mono font-medium">[{flag.allowed_org_ids.join(', ')}]</span>
                    </div>
                  )}
                </div>

                <div className="flex items-center gap-6">
                  {/* Rollout percentage control */}
                  <div className="text-right">
                    <div className="text-xs text-gray-400 mb-1 font-medium">
                      Rollout: <strong className="text-white">{flag.rollout_percentage}%</strong>
                    </div>
                    <input
                      type="range"
                      min="0"
                      max="100"
                      step="5"
                      value={flag.rollout_percentage}
                      onChange={(e) => handleUpdateRollout(flag.id, parseInt(e.target.value, 10))}
                      className="w-28 h-1.5 bg-[#0f3460] rounded-lg appearance-none cursor-pointer accent-[#16c79a]"
                    />
                  </div>

                  {/* Toggle Button */}
                  <button
                    onClick={() => handleToggleFlag(flag)}
                    className="p-1 text-gray-300 hover:text-white transition-colors"
                    title={flag.is_enabled ? 'Disable Flag' : 'Enable Flag'}
                  >
                    {flag.is_enabled ? (
                      <ToggleRight className="w-9 h-9 text-[#16c79a]" />
                    ) : (
                      <ToggleLeft className="w-9 h-9 text-gray-500" />
                    )}
                  </button>

                  {/* Delete Button */}
                  <button
                    onClick={() => handleDeleteFlag(flag.id, flag.key)}
                    className="p-2 text-gray-400 hover:text-red-400 transition-colors"
                    title="Delete Flag"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Plan Override Modal */}
      <Modal isOpen={isOverrideOpen} onClose={() => setIsOverrideOpen(false)} title="Administrative Plan Override">
        <form onSubmit={handleSaveOverride} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Target Organization</label>
            <input
              disabled
              value={`${selectedOrg?.name || ''} (#${selectedOrg?.id || ''})`}
              className="w-full h-10 rounded-md border border-[#0f3460] bg-[#16213e] px-3 py-2 text-sm text-gray-300"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">New Commercial Tier</label>
            <select
              value={overrideTier}
              onChange={(e) => setOverrideTier(e.target.value)}
              className="w-full h-10 rounded-md border border-[#0f3460] bg-[#1a1a2e] px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#16c79a]"
            >
              <option value="community">Community Edition ($0/mo, 3 Projects, 2 Seats)</option>
              <option value="team">Professional Team ($49/mo, 25 Projects, 10 Seats)</option>
              <option value="enterprise">Industrial Enterprise ($299/mo, Unlimited Projects & Seats)</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Extend Trial Duration (Days)</label>
            <Input
              type="number"
              value={extendDays}
              onChange={(e) => setExtendDays(parseInt(e.target.value, 10) || 0)}
              min="0"
              max="90"
            />
            <span className="text-xs text-gray-500 mt-1 block">Enter 0 to activate immediately as standard subscription without trial.</span>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-[#0f3460]">
            <Button variant="ghost" onClick={() => setIsOverrideOpen(false)} type="button">
              Cancel
            </Button>
            <Button type="submit" isLoading={overrideLoading}>
              Save Plan Override
            </Button>
          </div>
        </form>
      </Modal>

      {/* Create Feature Flag Modal */}
      <Modal isOpen={isNewFlagOpen} onClose={() => setIsNewFlagOpen(false)} title="Create Enterprise Feature Flag">
        <form onSubmit={handleCreateFlag} className="space-y-4">
          <Input
            label="Unique Flag Key"
            placeholder="e.g. quantum_simulator_bridge"
            value={flagKey}
            onChange={(e) => setFlagKey(e.target.value)}
            required
          />

          <Input
            label="Display Name"
            placeholder="e.g. Quantum Simulator Bridge"
            value={flagName}
            onChange={(e) => setFlagName(e.target.value)}
            required
          />

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Description</label>
            <textarea
              value={flagDesc}
              onChange={(e) => setFlagDesc(e.target.value)}
              placeholder="What does this feature flag toggle?"
              rows={3}
              className="w-full rounded-md border border-[#0f3460] bg-[#1a1a2e] px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#16c79a]"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Prerequisite Minimum Tier</label>
            <select
              value={flagMinTier}
              onChange={(e) => setFlagMinTier(e.target.value)}
              className="w-full h-10 rounded-md border border-[#0f3460] bg-[#1a1a2e] px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-[#16c79a]"
            >
              <option value="community">Community (Available to all tiers)</option>
              <option value="team">Team (Requires Team or Enterprise)</option>
              <option value="enterprise">Enterprise (Exclusive to Enterprise)</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-1.5">Initial Percentage Rollout: {flagRollout}%</label>
            <input
              type="range"
              min="0"
              max="100"
              step="5"
              value={flagRollout}
              onChange={(e) => setFlagRollout(parseInt(e.target.value, 10))}
              className="w-full h-2 bg-[#0f3460] rounded-lg appearance-none cursor-pointer accent-[#16c79a]"
            />
          </div>

          <Input
            label="Whitelisted Organization IDs (Optional, comma-separated)"
            placeholder="e.g. 1, 4, 12"
            value={flagAllowedOrgs}
            onChange={(e) => setFlagAllowedOrgs(e.target.value)}
          />

          <div className="flex justify-end gap-3 pt-4 border-t border-[#0f3460]">
            <Button variant="ghost" onClick={() => setIsNewFlagOpen(false)} type="button">
              Cancel
            </Button>
            <Button type="submit" isLoading={flagLoading}>
              Create Flag
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default AdminDashboardPage;
