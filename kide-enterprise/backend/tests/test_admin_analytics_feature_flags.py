import pytest
import uuid

@pytest.mark.asyncio
async def test_admin_analytics_kpis(client):
    # Register Owner of Org A
    owner_email = f"owner_{uuid.uuid4().hex[:6]}@corp-a.com"
    r_owner = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Chief Executive Owner",
        "org_name": "Global Industrial Corp"
    })
    assert r_owner.status_code == 200
    owner_token = r_owner.json()["access_token"]
    owner_headers = {"Authorization": f"Bearer {owner_token}"}

    # Invite a viewer to Org A to test non-owner access rejection
    viewer_email = f"viewer_{uuid.uuid4().hex[:6]}@corp-a.com"
    r_inv = await client.post("/api/v1/org/members/invite", json={
        "email": viewer_email,
        "full_name": "Audit Viewer",
        "role": "viewer",
        "password": "Password123!"
    }, headers=owner_headers)
    assert r_inv.status_code == 200

    r_login_v = await client.post("/api/v1/auth/login", json={
        "email": viewer_email,
        "password": "Password123!"
    })
    viewer_token = r_login_v.json()["access_token"]
    viewer_headers = {"Authorization": f"Bearer {viewer_token}"}

    # Verify viewer is rejected with 403 Forbidden
    r_forbidden = await client.get("/api/v1/admin/analytics", headers=viewer_headers)
    assert r_forbidden.status_code == 403

    # Owner gets analytics successfully
    r_analytics = await client.get("/api/v1/admin/analytics", headers=owner_headers)
    assert r_analytics.status_code == 200
    stats = r_analytics.json()

    assert "mrr_cents" in stats
    assert "arr_cents" in stats
    assert "active_subscribers" in stats
    assert "active_trials" in stats
    assert "tier_distribution" in stats
    assert stats["total_organizations"] >= 1
    assert stats["total_users"] >= 2
    assert stats["active_trials"] >= 1

@pytest.mark.asyncio
async def test_admin_organizations_directory(client):
    owner_email = f"dir_owner_{uuid.uuid4().hex[:6]}@dir-corp.com"
    r_owner = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Directory Administrator",
        "org_name": "Directory Enterprise"
    })
    owner_token = r_owner.json()["access_token"]
    headers = {"Authorization": f"Bearer {owner_token}"}

    # Fetch directory
    r_dir = await client.get("/api/v1/admin/organizations", headers=headers)
    assert r_dir.status_code == 200
    orgs = r_dir.json()
    assert len(orgs) >= 1

    match = next((o for o in orgs if o["name"] == "Directory Enterprise"), None)
    assert match is not None
    assert match["owner_email"] == owner_email
    assert match["plan_tier"] == "team" # Default trial
    assert match["is_trial"] is True
    assert match["members_count"] == 1
    assert match["projects_count"] == 0

@pytest.mark.asyncio
async def test_admin_plan_override_and_trial_extension(client):
    owner_email = f"override_owner_{uuid.uuid4().hex[:6]}@test.com"
    r_owner = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "System Admin",
        "org_name": "Target Organization"
    })
    owner_token = r_owner.json()["access_token"]
    headers = {"Authorization": f"Bearer {owner_token}"}

    # Get subscription
    r_sub = await client.get("/api/v1/billing/subscription", headers=headers)
    org_id = r_sub.json()["org_id"]

    # Admin override: upgrade target org to enterprise and extend trial by 30 days
    r_override = await client.put(f"/api/v1/admin/organizations/{org_id}/tier", json={
        "plan_tier": "enterprise",
        "extend_trial_days": 30
    }, headers=headers)
    assert r_override.status_code == 200
    updated = r_override.json()
    assert updated["plan_tier"] == "enterprise"
    assert updated["is_trial"] is True
    assert updated["trial_days_remaining"] >= 30

    # Verify subscription reflects this override
    r_sub_after = await client.get("/api/v1/billing/subscription", headers=headers)
    assert r_sub_after.json()["plan_tier"] == "enterprise"

@pytest.mark.asyncio
async def test_feature_flags_crud(client):
    owner_email = f"ff_admin_{uuid.uuid4().hex[:6]}@flags.com"
    r_owner = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Flag Operator",
        "org_name": "Flags R Us"
    })
    headers = {"Authorization": f"Bearer {r_owner.json()['access_token']}"}

    # 1. List default seeded flags
    r_list = await client.get("/api/v1/admin/feature-flags", headers=headers)
    assert r_list.status_code == 200
    flags = r_list.json()
    assert len(flags) >= 6
    keys = [f["key"] for f in flags]
    assert "deep_semantic_validator" in keys
    assert "ai_copilot_v2" in keys
    assert "traceability_blast_radius" in keys

    # 2. Create new flag
    custom_key = f"beta_feature_{uuid.uuid4().hex[:6]}"
    r_create = await client.post("/api/v1/admin/feature-flags", json={
        "key": custom_key,
        "name": "Experimental Neural Synthesis",
        "description": "Next-gen deep state machine synthesis",
        "is_enabled": True,
        "minimum_tier": "enterprise",
        "allowed_org_ids": [999],
        "rollout_percentage": 50
    }, headers=headers)
    assert r_create.status_code == 201
    created = r_create.json()
    flag_id = created["id"]
    assert created["key"] == custom_key
    assert created["minimum_tier"] == "enterprise"
    assert created["rollout_percentage"] == 50

    # 3. Update flag
    r_update = await client.put(f"/api/v1/admin/feature-flags/{flag_id}", json={
        "name": "Updated Neural Synthesis",
        "is_enabled": False,
        "rollout_percentage": 100
    }, headers=headers)
    assert r_update.status_code == 200
    updated = r_update.json()
    assert updated["name"] == "Updated Neural Synthesis"
    assert updated["is_enabled"] is False
    assert updated["rollout_percentage"] == 100

    # 4. Delete flag
    r_del = await client.delete(f"/api/v1/admin/feature-flags/{flag_id}", headers=headers)
    assert r_del.status_code == 204

@pytest.mark.asyncio
async def test_feature_flags_evaluation(client):
    owner_email = f"eval_user_{uuid.uuid4().hex[:6]}@eval.com"
    r_owner = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Eval Engineer",
        "org_name": "Evaluation Dynamics"
    })
    headers = {"Authorization": f"Bearer {r_owner.json()['access_token']}"}

    # Client evaluation endpoint
    r_eval = await client.get("/api/v1/admin/feature-flags/eval", headers=headers)
    assert r_eval.status_code == 200
    flags_map = r_eval.json()

    # Since new org is on Team trial, team & community flags should evaluate to True
    assert flags_map.get("deep_semantic_validator") is True
    assert flags_map.get("ai_copilot_v2") is True
    assert flags_map.get("traceability_blast_radius") is True
    # Enterprise-only flag should evaluate to False for team tier
    assert flags_map.get("enterprise_audit_stream") is False
