import httpx
import json
import time
import uuid
import sys
import subprocess
import os

BASE_URL = "http://127.0.0.1:8005/api/v1"

def print_phase(num, title):
    print(f"\n=======================================================")
    print(f"PHASE {num}: {title}")
    print(f"=======================================================")

def main():
    print("Starting PR 5: Subscription Architecture, Entitlements Engine & Stripe Billing Live Verification...")

    # Check if server is running on 8005; if not, spawn it using active Python
    server_process = None
    client = httpx.Client(base_url="http://127.0.0.1:8005", timeout=15.0)
    try:
        r = client.get("/")
        if r.status_code == 200:
            print("Server is already running on port 8005.")
    except Exception:
        print("Starting server on port 8005...")
        backend_dir = os.path.join(os.getcwd(), "kide-enterprise", "backend")
        server_process = subprocess.Popen(
            [sys.executable, "-m", "uvicorn", "app.main:app", "--host", "127.0.0.1", "--port", "8005"],
            cwd=backend_dir,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
        time.sleep(3)
        for _ in range(12):
            try:
                r = client.get("/")
                if r.status_code == 200:
                    print("Server successfully started on port 8005.")
                    break
            except Exception:
                time.sleep(1)

    try:
        # Phase 1: Server Health Check
        print_phase(1, "Server Health Check")
        r = client.get("/")
        assert r.status_code == 200, f"Expected 200, got {r.status_code}"
        print(f"Health Response: {r.json()}")

        # Phase 2: Register Organization & Verify 14-Day Team Trial
        print_phase(2, "Register Organization & Verify 14-Day Professional Team Trial Allocation")
        test_email = f"lead.architect_{uuid.uuid4().hex[:6]}@industrial-ai.corp"
        r_reg = client.post("/api/v1/auth/register", json={
            "email": test_email,
            "password": "Password123!",
            "full_name": "Dr. Aris Thorne",
            "org_name": "Industrial AI Dynamics"
        })
        assert r_reg.status_code == 200, f"Registration failed: {r_reg.text}"
        auth_token = r_reg.json()["access_token"]
        headers = {"Authorization": f"Bearer {auth_token}"}
        print(f"Registered Organization Owner: {test_email}")

        # Check Subscription Details
        r_sub = client.get("/api/v1/billing/subscription", headers=headers)
        assert r_sub.status_code == 200, f"Failed to get subscription: {r_sub.text}"
        sub_data = r_sub.json()
        print(f"Allocated Subscription Tier: {sub_data['plan_tier']}, Trial: {sub_data['is_trial']}, Days Remaining: {sub_data['trial_days_remaining']}")
        assert sub_data["is_trial"] is True, "New org must be provisioned with is_trial=True"
        assert sub_data["plan_tier"] == "team", "New org trial tier must be 'team'"
        assert sub_data["trial_days_remaining"] == 14, "New org trial duration must be 14 days"
        org_id = sub_data["org_id"]

        # Phase 3: Plan Catalog Retrieval & Integrity
        print_phase(3, "Plan Catalog Retrieval & Pricing Integrity")
        r_plans = client.get("/api/v1/billing/plans")
        assert r_plans.status_code == 200, f"Failed to fetch plans: {r_plans.text}"
        plans = r_plans.json()
        tiers = {p["tier"]: p for p in plans}
        print(f"Loaded {len(plans)} plans from catalog: {list(tiers.keys())}")
        assert "community" in tiers and "team" in tiers and "enterprise" in tiers

        # Verify plan specifics
        assert tiers["community"]["price_monthly_cents"] == 0
        assert tiers["community"]["max_projects"] == 3
        assert tiers["community"]["max_members"] == 2
        assert tiers["team"]["price_monthly_cents"] == 4900
        assert tiers["team"]["max_projects"] == 25
        assert tiers["team"]["max_members"] == 10
        assert tiers["enterprise"]["price_monthly_cents"] == 29900
        assert tiers["enterprise"]["max_projects"] == -1
        assert tiers["enterprise"]["max_members"] == -1
        print("Plan catalog verified: Community ($0), Team ($49/mo), Enterprise ($299/mo) with accurate quotas.")

        # Phase 4: Usage Meter Tracking
        print_phase(4, "Usage Meter Initial Tracking")
        usage = sub_data["usage"]
        print(f"Active Usage: Projects={usage['projects_count']}, Members={usage['members_count']}, AI Tokens={usage['ai_tokens_used']}")
        assert usage["projects_count"] == 0
        assert usage["members_count"] == 1  # The owner

        # Phase 5: Enforce Project Limits on Community Tier (402 Payment Required)
        print_phase(5, "Degrade to Community Tier & Enforce Project Quota Limits")
        # Simulate subscription downgrade to Community tier by sending subscription updated webhook
        sub_stripe_id = f"sub_test_{uuid.uuid4().hex[:8]}"
        cust_stripe_id = f"cus_test_{uuid.uuid4().hex[:8]}"
        downgrade_event = {
            "id": f"evt_downgrade_{uuid.uuid4().hex[:8]}",
            "type": "customer.subscription.updated",
            "data": {
                "object": {
                    "id": sub_stripe_id,
                    "customer": cust_stripe_id,
                    "metadata": {"org_id": str(org_id), "tier": "community"},
                    "items": {
                        "data": [{"price": {"lookup_key": "community"}}]
                    },
                    "status": "active"
                }
            }
        }
        r_hook_down = client.post("/api/v1/billing/webhook", json=downgrade_event)
        assert r_hook_down.status_code == 200, f"Downgrade webhook failed: {r_hook_down.text}"

        r_sub_check = client.get("/api/v1/billing/subscription", headers=headers)
        assert r_sub_check.json()["plan_tier"] == "community", "Org should now be on community tier"
        assert r_sub_check.json()["is_trial"] is False, "Trial must be disabled after manual tier change"

        # Now create 3 projects (the Community max)
        for i in range(1, 4):
            r_proj = client.post("/api/v1/projects", json={
                "name": f"Community Project #{i}",
                "description": "Allowed project"
            }, headers=headers)
            assert r_proj.status_code == 200, f"Project {i} failed: {r_proj.text}"
            print(f"Created allowed Project #{i}: {r_proj.json()['name']}")

        # Attempt to create 4th project -> Must return 402 Payment Required
        r_proj_blocked = client.post("/api/v1/projects", json={
            "name": "Excessive 4th Project",
            "description": "Should be rejected by entitlements engine"
        }, headers=headers)
        print(f"4th Project Attempt Status: {r_proj_blocked.status_code}, Detail: {r_proj_blocked.json()['detail']}")
        assert r_proj_blocked.status_code == 402, f"Expected 402 Payment Required, got {r_proj_blocked.status_code}"
        assert "Plan limit reached:" in r_proj_blocked.json()["detail"]

        # Phase 6: Enforce Member Seat Limits on Community Tier (402 Payment Required)
        print_phase(6, "Enforce Member Seat Quota on Community Tier")
        # Current members = 1 (Owner). Community max = 2.
        # Invite 2nd member -> Allowed
        colleague_email = f"colleague_{uuid.uuid4().hex[:6]}@industrial-ai.corp"
        r_inv1 = client.post("/api/v1/org/members/invite", json={
            "email": colleague_email,
            "full_name": "Alex Chen",
            "role": "engineer"
        }, headers=headers)
        assert r_inv1.status_code == 200, f"Colleague invite failed: {r_inv1.text}"
        print(f"Successfully invited 2nd member: {colleague_email}")

        # Attempt to invite 3rd member -> Must return 402 Payment Required
        third_email = f"third_{uuid.uuid4().hex[:6]}@industrial-ai.corp"
        r_inv_blocked = client.post("/api/v1/org/members/invite", json={
            "email": third_email,
            "full_name": "Marcus Vance",
            "role": "engineer"
        }, headers=headers)
        print(f"3rd Member Invite Attempt Status: {r_inv_blocked.status_code}, Detail: {r_inv_blocked.json()['detail']}")
        assert r_inv_blocked.status_code == 402, f"Expected 402 Payment Required, got {r_inv_blocked.status_code}"
        assert "Seat limit reached:" in r_inv_blocked.json()["detail"]

        # Phase 7: Create Stripe Checkout Session
        print_phase(7, "Create Stripe Checkout Session")
        r_checkout = client.post("/api/v1/billing/checkout", json={
            "tier": "enterprise",
            "billing_cycle": "annual",
            "success_url": "http://localhost:5173/settings?tab=billing&session_id={CHECKOUT_SESSION_ID}",
            "cancel_url": "http://localhost:5173/settings?tab=billing"
        }, headers=headers)
        assert r_checkout.status_code == 200, f"Checkout failed: {r_checkout.text}"
        checkout_data = r_checkout.json()
        print(f"Created Stripe Checkout Session ID: {checkout_data['session_id']}")
        print(f"Checkout URL: {checkout_data['checkout_url']}")
        assert checkout_data["session_id"].startswith("cs_")
        assert "checkout_url" in checkout_data

        # Phase 8: Webhook Event Handling & Instant Upgrade to Enterprise
        print_phase(8, "Process Stripe Webhook -> Instant Upgrade to Enterprise Tier")
        evt_checkout_id = f"evt_checkout_{uuid.uuid4().hex[:8]}"
        checkout_webhook_event = {
            "id": evt_checkout_id,
            "type": "checkout.session.completed",
            "data": {
                "object": {
                    "id": checkout_data["session_id"],
                    "customer": f"cus_{uuid.uuid4().hex[:8]}",
                    "subscription": f"sub_{uuid.uuid4().hex[:8]}",
                    "metadata": {
                        "org_id": str(org_id),
                        "tier": "enterprise",
                        "billing_cycle": "annual"
                    }
                }
            }
        }
        r_webhook = client.post("/api/v1/billing/webhook", json=checkout_webhook_event)
        assert r_webhook.status_code == 200, f"Webhook processing failed: {r_webhook.text}"
        print(f"Webhook Result: {r_webhook.json()}")
        assert r_webhook.json()["status"] == "processed"

        # Verify immediate upgrade in subscription endpoint
        r_sub_upgraded = client.get("/api/v1/billing/subscription", headers=headers)
        upgraded_data = r_sub_upgraded.json()
        print(f"Upgraded Tier: {upgraded_data['plan_tier']}, Status: {upgraded_data['status']}")
        assert upgraded_data["plan_tier"] == "enterprise"
        assert upgraded_data["status"] == "active"
        assert upgraded_data["usage"]["max_projects"] == -1
        assert upgraded_data["usage"]["max_members"] == -1

        # Now test creating a 4th project on Enterprise (should succeed!)
        r_proj_enterprise = client.post("/api/v1/projects", json={
            "name": "Enterprise Scale Super-Project",
            "description": "Now permitted under Enterprise unlimited quota"
        }, headers=headers)
        assert r_proj_enterprise.status_code == 200, f"Enterprise project creation failed: {r_proj_enterprise.text}"
        print(f"Successfully created 4th project under Enterprise tier: {r_proj_enterprise.json()['name']}")

        # Phase 9: Webhook Idempotency & Deduplication
        print_phase(9, "Verify Webhook Deduplication & Idempotency Ledger")
        r_webhook_dup = client.post("/api/v1/billing/webhook", json=checkout_webhook_event)
        assert r_webhook_dup.status_code == 200
        dup_result = r_webhook_dup.json()
        print(f"Duplicate Webhook Result: {dup_result}")
        assert dup_result["status"] == "already_processed", "Duplicate webhook must return 'already_processed'"

        # Phase 10: Customer Portal Session & Subscription Cancellation Degradation
        print_phase(10, "Customer Portal Session & Graceful Subscription Cancellation")
        r_portal = client.post("/api/v1/billing/portal", json={
            "return_url": "http://localhost:5173/settings?tab=billing"
        }, headers=headers)
        assert r_portal.status_code == 200, f"Portal session creation failed: {r_portal.text}"
        print(f"Generated Customer Portal URL: {r_portal.json()['portal_url']}")

        # Simulate cancellation webhook from Stripe
        evt_cancel_id = f"evt_cancel_{uuid.uuid4().hex[:8]}"
        cancel_webhook_event = {
            "id": evt_cancel_id,
            "type": "customer.subscription.deleted",
            "data": {
                "object": {
                    "id": checkout_webhook_event["data"]["object"]["subscription"],
                    "customer": checkout_webhook_event["data"]["object"]["customer"],
                    "metadata": {"org_id": str(org_id)},
                    "status": "canceled"
                }
            }
        }
        r_cancel_hook = client.post("/api/v1/billing/webhook", json=cancel_webhook_event)
        assert r_cancel_hook.status_code == 200
        print(f"Cancellation Webhook Result: {r_cancel_hook.json()}")

        # Verify organization degraded to community tier
        r_sub_canceled = client.get("/api/v1/billing/subscription", headers=headers)
        canceled_data = r_sub_canceled.json()
        print(f"Post-Cancellation Tier: {canceled_data['plan_tier']}, Status: {canceled_data['status']}")
        assert canceled_data["plan_tier"] == "community"
        assert canceled_data["status"] == "canceled"

        print("\n=======================================================")
        print("ALL 10 PHASES OF PR 5 LIVE VERIFICATION PASSED WITH 100% SUCCESS!")
        print("=======================================================")

    finally:
        client.close()
        if server_process:
            print("Terminating test uvicorn server...")
            server_process.terminate()
            server_process.wait()

if __name__ == "__main__":
    main()
