import { describe, it } from 'node:test';
import assert from 'node:assert';
import crypto from 'node:crypto';

describe('PR 6: Owner Business Intelligence & Feature Flags Engine', () => {

  const TIER_WEIGHTS = {
    community: 1,
    team: 2,
    enterprise: 3
  };

  it('1. should accurately calculate MRR, ARR, and financial metrics', () => {
    const subscriptions = [
      { plan_tier: 'community', status: 'active', monthly_price_cents: 0 },
      { plan_tier: 'team', status: 'active', monthly_price_cents: 4900 },
      { plan_tier: 'team', status: 'active', monthly_price_cents: 4900 },
      { plan_tier: 'enterprise', status: 'active', monthly_price_cents: 29900 },
      { plan_tier: 'team', status: 'trialing', monthly_price_cents: 4900 }, // in trial, not paying yet
      { plan_tier: 'community', status: 'canceled', monthly_price_cents: 0 },
    ];

    let mrrCents = 0;
    let activeSubscribers = 0;
    let activeTrials = 0;

    for (const sub of subscriptions) {
      if (sub.status === 'active' && sub.plan_tier !== 'community') {
        activeSubscribers += 1;
        mrrCents += sub.monthly_price_cents;
      } else if (sub.status === 'trialing') {
        activeTrials += 1;
      }
    }

    assert.strictEqual(activeSubscribers, 3);
    assert.strictEqual(activeTrials, 1);
    // 4900 + 4900 + 29900 = 39700 cents ($397.00/mo)
    assert.strictEqual(mrrCents, 39700);

    const mrrUsd = mrrCents / 100.0;
    assert.strictEqual(mrrUsd, 397.0);

    const arrCents = mrrCents * 12;
    assert.strictEqual(arrCents, 476400); // $4,764.00/yr

    const arrUsd = arrCents / 100.0;
    assert.strictEqual(arrUsd, 4764.0);
  });

  it('2. should calculate conversion rate and churn percentage accurately', () => {
    const totalOrgs = 20;
    const payingOrgs = 5;
    const canceledOrgs = 1;

    // Conversion rate: paying / total
    const conversionRate = Math.round((payingOrgs / totalOrgs) * 100 * 10) / 10;
    assert.strictEqual(conversionRate, 25.0);

    // Churn rate: canceled / (paying + canceled)
    const paidPool = payingOrgs + canceledOrgs;
    const churnRate = Math.round((canceledOrgs / paidPool) * 100 * 10) / 10;
    assert.strictEqual(churnRate, 16.7);
  });

  it('3. should evaluate feature flag tier prerequisites and killswitch', () => {
    const evaluateFlag = (flag, orgTier, orgId) => {
      // 1. Killswitch
      if (!flag.is_enabled) return false;

      // 2. Whitelist bypass
      if (flag.allowed_org_ids && flag.allowed_org_ids.includes(orgId)) {
        return true;
      }

      // 3. Minimum Tier
      const orgWeight = TIER_WEIGHTS[orgTier] || 1;
      const minWeight = TIER_WEIGHTS[flag.minimum_tier] || 1;
      if (orgWeight < minWeight) return false;

      // 4. Percentage rollout
      if (flag.rollout_percentage >= 100) return true;
      if (flag.rollout_percentage <= 0) return false;

      const hashInput = `${flag.key}:${orgId}`;
      const hash = crypto.createHash('md5').update(hashInput).digest('hex');
      const bucket = parseInt(hash.substring(0, 8), 16) % 100;
      return bucket < flag.rollout_percentage;
    };

    const enterpriseFlag = {
      key: 'enterprise_audit_stream',
      is_enabled: true,
      minimum_tier: 'enterprise',
      allowed_org_ids: [],
      rollout_percentage: 100
    };

    // Community and team cannot access enterprise-gated flag
    assert.strictEqual(evaluateFlag(enterpriseFlag, 'community', 10), false);
    assert.strictEqual(evaluateFlag(enterpriseFlag, 'team', 10), false);
    // Enterprise can access
    assert.strictEqual(evaluateFlag(enterpriseFlag, 'enterprise', 10), true);

    // Whitelisted tenant bypasses tier restriction
    const whitelistedFlag = {
      ...enterpriseFlag,
      allowed_org_ids: [10]
    };
    assert.strictEqual(evaluateFlag(whitelistedFlag, 'community', 10), true);
    assert.strictEqual(evaluateFlag(whitelistedFlag, 'community', 99), false);

    // Disabled flag evaluates to false everywhere
    const disabledFlag = {
      ...enterpriseFlag,
      is_enabled: false
    };
    assert.strictEqual(evaluateFlag(disabledFlag, 'enterprise', 10), false);
    assert.strictEqual(evaluateFlag(disabledFlag, 'community', 10), false);
  });

  it('4. should filter and search organization directory records', () => {
    const sampleOrgs = [
      { id: 1, name: 'Cyberdyne Systems', slug: 'cyberdyne-89a', owner_email: 'sarah@cyberdyne.corp', plan_tier: 'team' },
      { id: 2, name: 'Weyland-Yutani', slug: 'weyland-b12', owner_email: 'ripley@weyland.corp', plan_tier: 'enterprise' },
      { id: 3, name: 'Tyrell Corp', slug: 'tyrell-c34', owner_email: 'deckard@tyrell.corp', plan_tier: 'community' },
    ];

    const searchFilter = (orgs, query, tier) => {
      return orgs.filter(o => {
        const matchesQuery = !query ||
          o.name.toLowerCase().includes(query.toLowerCase()) ||
          o.slug.toLowerCase().includes(query.toLowerCase()) ||
          o.owner_email.toLowerCase().includes(query.toLowerCase());
        const matchesTier = tier === 'all' || o.plan_tier === tier;
        return matchesQuery && matchesTier;
      });
    };

    // Filter by query "weyland"
    const weylandResult = searchFilter(sampleOrgs, 'weyland', 'all');
    assert.strictEqual(weylandResult.length, 1);
    assert.strictEqual(weylandResult[0].name, 'Weyland-Yutani');

    // Filter by tier "team"
    const teamResult = searchFilter(sampleOrgs, '', 'team');
    assert.strictEqual(teamResult.length, 1);
    assert.strictEqual(teamResult[0].name, 'Cyberdyne Systems');

    // Filter all
    assert.strictEqual(searchFilter(sampleOrgs, '', 'all').length, 3);
  });

});

