import pytest
import uuid
from datetime import datetime, timedelta, timezone
from sqlalchemy import select
from app.models.subscription import Subscription, SubscriptionStatus, PlanTier

@pytest.mark.asyncio
async def test_subscription_plans_seeded(client):
    res = await client.get("/api/v1/billing/plans")
    assert res.status_code == 200
    plans = res.json()
    assert len(plans) == 3
    tiers = {p["tier"]: p for p in plans}
    
    assert "community" in tiers
    assert "team" in tiers
    assert "enterprise" in tiers

    assert tiers["community"]["max_projects"] == 3
    assert tiers["community"]["max_members"] == 2
    assert tiers["community"]["price_monthly_cents"] == 0

    assert tiers["team"]["max_projects"] == 25
    assert tiers["team"]["max_members"] == 10
    assert tiers["team"]["price_monthly_cents"] == 4900
    assert tiers["team"]["traceability_export"] is True

    assert tiers["enterprise"]["max_projects"] == -1
    assert tiers["enterprise"]["max_members"] == -1
    assert tiers["enterprise"]["price_monthly_cents"] == 29900

@pytest.mark.asyncio
async def test_new_org_initial_trial(client):
    owner_email = f"trial_owner_{uuid.uuid4()}@robotics.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Robotics Owner",
        "org_name": "Autonomous Robotics Corp"
    })
    assert reg.status_code == 200
    owner_token = reg.json()["access_token"]
    headers = {"Authorization": f"Bearer {owner_token}"}

    # Query subscription
    sub_res = await client.get("/api/v1/billing/subscription", headers=headers)
    assert sub_res.status_code == 200
    sub_data = sub_res.json()
    
    assert sub_data["plan_tier"] == "team"
    assert sub_data["status"] == "trialing"
    assert sub_data["is_trial"] is True
    assert sub_data["trial_days_remaining"] >= 13
    assert sub_data["usage"]["projects_count"] == 0
    assert sub_data["usage"]["members_count"] == 1

@pytest.mark.asyncio
async def test_entitlements_project_limits_on_community(client, db_session):
    owner_email = f"comm_owner_{uuid.uuid4()}@comm.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Community Owner",
        "org_name": "Community Open Lab"
    })
    headers = {"Authorization": f"Bearer {reg.json()['access_token']}"}

    # Fetch user to find org_id
    me_res = await client.get("/api/v1/auth/me", headers=headers)
    
    # Degrade subscription to community tier
    sub_res = await client.get("/api/v1/billing/subscription", headers=headers)
    sub_id = sub_res.json()["id"]

    sub_stmt = select(Subscription).where(Subscription.id == sub_id)
    sub_record = (await db_session.execute(sub_stmt)).scalars().first()
    sub_record.plan_tier = PlanTier.COMMUNITY.value
    sub_record.status = SubscriptionStatus.ACTIVE.value
    sub_record.trial_end = None
    await db_session.commit()

    # Community allows up to 3 projects
    # Create 3 projects
    for i in range(1, 4):
        p_res = await client.post(
            "/api/v1/projects",
            json={"name": f"Allowed Project {i}"},
            headers=headers
        )
        assert p_res.status_code == 200, f"Project {i} should be created successfully"

    # Attempt to create 4th project -> Must fail with 402 Payment Required
    blocked_res = await client.post(
        "/api/v1/projects",
        json={"name": "Excess Project 4"},
        headers=headers
    )
    assert blocked_res.status_code == 402
    assert "Plan limit reached" in blocked_res.json()["detail"]

@pytest.mark.asyncio
async def test_entitlements_member_seat_limits_on_community(client, db_session):
    owner_email = f"seat_owner_{uuid.uuid4()}@seats.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Seat Owner",
        "org_name": "Seat Constraint Lab"
    })
    headers = {"Authorization": f"Bearer {reg.json()['access_token']}"}

    # Degrade to community (max 2 members: owner + 1)
    sub_res = await client.get("/api/v1/billing/subscription", headers=headers)
    sub_id = sub_res.json()["id"]
    sub_record = (await db_session.execute(select(Subscription).where(Subscription.id == sub_id))).scalars().first()
    sub_record.plan_tier = PlanTier.COMMUNITY.value
    sub_record.status = SubscriptionStatus.ACTIVE.value
    sub_record.trial_end = None
    await db_session.commit()

    # Invite 1st member -> Total 2 members (Succeeds)
    inv1 = await client.post(
        "/api/v1/org/members/invite",
        json={"email": f"eng1_{uuid.uuid4()}@seats.corp", "full_name": "Engineer 1", "role": "engineer"},
        headers=headers
    )
    assert inv1.status_code == 200

    # Invite 2nd member -> Would make 3 members, exceeding Community limit of 2 -> Fails 402
    inv2 = await client.post(
        "/api/v1/org/members/invite",
        json={"email": f"eng2_{uuid.uuid4()}@seats.corp", "full_name": "Engineer 2", "role": "engineer"},
        headers=headers
    )
    assert inv2.status_code == 402
    assert "Seat limit reached" in inv2.json()["detail"]

@pytest.mark.asyncio
async def test_stripe_checkout_and_customer_portal(client):
    owner_email = f"checkout_owner_{uuid.uuid4()}@pay.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Checkout Owner",
        "org_name": "Payment Tech Corp"
    })
    owner_headers = {"Authorization": f"Bearer {reg.json()['access_token']}"}

    # 1. Owner initiates checkout for TEAM
    checkout_res = await client.post(
        "/api/v1/billing/checkout",
        json={"tier": "team", "billing_cycle": "annual"},
        headers=owner_headers
    )
    assert checkout_res.status_code == 200
    ch_data = checkout_res.json()
    assert "session_id" in ch_data
    assert "checkout_url" in ch_data
    assert ch_data["session_id"].startswith("cs_")

    # 2. Owner accesses customer portal
    portal_res = await client.post(
        "/api/v1/billing/portal",
        json={"return_url": "http://localhost:5173/settings"},
        headers=owner_headers
    )
    assert portal_res.status_code == 200
    assert "portal_url" in portal_res.json()

    # 3. Non-owner (Viewer) cannot initiate checkout (403)
    inv_viewer = await client.post(
        "/api/v1/org/members/invite",
        json={"email": f"v_{uuid.uuid4()}@pay.corp", "full_name": "Viewer", "role": "viewer", "password": "Pass1234!"},
        headers=owner_headers
    )
    login_v = await client.post("/api/v1/auth/login", json={"email": inv_viewer.json()["email"], "password": "Pass1234!"})
    viewer_headers = {"Authorization": f"Bearer {login_v.json()['access_token']}"}

    viewer_checkout = await client.post(
        "/api/v1/billing/checkout",
        json={"tier": "enterprise"},
        headers=viewer_headers
    )
    assert viewer_checkout.status_code == 403

@pytest.mark.asyncio
async def test_stripe_webhook_processing_and_idempotency(client):
    # Setup Org
    owner_email = f"webhook_owner_{uuid.uuid4()}@hook.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Webhook Owner",
        "org_name": "Webhook Verified Org"
    })
    headers = {"Authorization": f"Bearer {reg.json()['access_token']}"}
    sub_res = await client.get("/api/v1/billing/subscription", headers=headers)
    org_id = sub_res.json()["org_id"]

    # 1. Send checkout.session.completed webhook
    event_id = f"evt_test_{uuid.uuid4().hex}"
    payload = {
        "id": event_id,
        "type": "checkout.session.completed",
        "data": {
            "object": {
                "client_reference_id": str(org_id),
                "customer": "cus_test_12345",
                "subscription": "sub_test_67890",
                "metadata": {"tier": "enterprise"}
            }
        }
    }
    wh_res = await client.post("/api/v1/billing/webhook", json=payload)
    assert wh_res.status_code == 200
    assert wh_res.json()["status"] == "processed"

    # Verify subscription is now ACTIVE ENTERPRISE
    sub_updated = await client.get("/api/v1/billing/subscription", headers=headers)
    assert sub_updated.json()["plan_tier"] == "enterprise"
    assert sub_updated.json()["status"] == "active"

    # 2. Idempotency test: Send the exact same event again
    wh_dup = await client.post("/api/v1/billing/webhook", json=payload)
    assert wh_dup.status_code == 200
    assert wh_dup.json()["status"] == "already_processed"

    # 3. Send customer.subscription.deleted event -> Degrades to Community
    del_event_id = f"evt_del_{uuid.uuid4().hex}"
    del_payload = {
        "id": del_event_id,
        "type": "customer.subscription.deleted",
        "data": {
            "object": {
                "id": "sub_test_67890",
                "customer": "cus_test_12345"
            }
        }
    }
    wh_del = await client.post("/api/v1/billing/webhook", json=del_payload)
    assert wh_del.status_code == 200

    # Verify degraded to community
    sub_degraded = await client.get("/api/v1/billing/subscription", headers=headers)
    assert sub_degraded.json()["plan_tier"] == "community"
    assert sub_degraded.json()["status"] == "canceled"

