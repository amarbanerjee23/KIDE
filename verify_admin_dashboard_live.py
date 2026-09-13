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
    print("Starting PR 6: Owner Business Intelligence & Feature Flags Live Verification...")

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

        # Phase 2: Register Multi-Tenant Portfolio
        print_phase(2, "Register Multi-Tenant Customer Organizations")
        # 1. Platform Executive Owner (Apex Robotics)
        owner_email = f"exec.owner_{uuid.uuid4().hex[:6]}@apex-robotics.corp"
        r_owner = client.post("/api/v1/auth/register", json={
            "email": owner_email,
            "password": "Password123!",
            "full_name": "Executive Officer",
            "org_name": "Apex Robotics Enterprise"
        })
        assert r_owner.status_code == 200, f"Owner reg failed: {r_owner.text}"
        owner_token = r_owner.json()["access_token"]
        owner_headers = {"Authorization": f"Bearer {owner_token}"}
        print(f"Registered Platform Executive Owner: {owner_email}")

        # Upgrade Apex to Enterprise tier via webhook
        sub_stripe_id = f"sub_apex_{uuid.uuid4().hex[:8]}"
        cust_stripe_id = f"cus_apex_{uuid.uuid4().hex[:8]}"
        r_sub_apex = client.get("/api/v1/billing/subscription", headers=owner_headers)
        apex_org_id = r_sub_apex.json()["org_id"]

        r_upgrade = client.post("/api/v1/billing/webhook", json={
            "id": f"evt_{uuid.uuid4().hex[:8]}",
            "type": "checkout.session.completed",
            "data": {
                "object": {
                    "id": f"cs_{uuid.uuid4().hex[:8]}",
                    "customer": cust_stripe_id,
                    "subscription": sub_stripe_id,
                    "metadata": {"org_id": str(apex_org_id), "tier": "enterprise"}
                }
            }
        })
        assert r_upgrade.status_code == 200
        print("Upgraded Apex Robotics to Active Enterprise Tier ($299/mo).")

        # 2. Team Customer (Cyberdyne Systems - default 14-day trial)
        cyber_email = f"lead_{uuid.uuid4().hex[:6]}@cyberdyne.corp"
        r_cyber = client.post("/api/v1/auth/register", json={
            "email": cyber_email,
            "password": "Password123!",
            "full_name": "Sarah Connor",
            "org_name": "Cyberdyne Systems"
        })
        assert r_cyber.status_code == 200
        cyber_token = r_cyber.json()["access_token"]
        cyber_headers = {"Authorization": f"Bearer {cyber_token}"}
        print(f"Registered Team Trial Customer: {cyber_email}")

        # 3. Community Customer (Indie Control Lab)
        indie_email = f"hacker_{uuid.uuid4().hex[:6]}@indie-lab.org"
        r_indie = client.post("/api/v1/auth/register", json={
            "email": indie_email,
            "password": "Password123!",
            "full_name": "Independent Researcher",
            "org_name": "Indie Control Lab"
        })
        assert r_indie.status_code == 200
        indie_token = r_indie.json()["access_token"]
        indie_headers = {"Authorization": f"Bearer {indie_token}"}

        r_sub_indie = client.get("/api/v1/billing/subscription", headers=indie_headers)
        indie_org_id = r_sub_indie.json()["org_id"]

        # Downgrade Indie Lab to community
        client.post("/api/v1/billing/webhook", json={
            "id": f"evt_{uuid.uuid4().hex[:8]}",
            "type": "customer.subscription.deleted",
            "data": {
                "object": {
                    "id": f"sub_indie_{uuid.uuid4().hex[:8]}",
                    "customer": f"cus_indie_{uuid.uuid4().hex[:8]}",
                    "metadata": {"org_id": str(indie_org_id)},
                    "status": "canceled"
                }
            }
        })
        print(f"Registered Community Customer: {indie_email}")

        # Phase 3: Seed Projects Across Tenants
        print_phase(3, "Seed Projects Across Customer Tenants")
        r_proj_apex = client.post("/api/v1/projects", json={
            "name": "Apex Core Robotic Cell",
            "description": "Enterprise mission critical node"
        }, headers=owner_headers)
        assert r_proj_apex.status_code == 200

        r_proj_cyber = client.post("/api/v1/projects", json={
            "name": "Cyberdyne T-800 Arm",
            "description": "Team trial project"
        }, headers=cyber_headers)
        assert r_proj_cyber.status_code == 200

        r_proj_indie = client.post("/api/v1/projects", json={
            "name": "Indie Sensor Prototype",
            "description": "Community hobby project"
        }, headers=indie_headers)
        assert r_proj_indie.status_code == 200
        print("Created projects across Enterprise, Team, and Community tenants.")

        # Phase 4: Executive Business KPI Analytics
        print_phase(4, "Executive Business KPI Analytics")
        r_analytics = client.get("/api/v1/admin/analytics", headers=owner_headers)
        assert r_analytics.status_code == 200, f"Analytics failed: {r_analytics.text}"
        stats = r_analytics.json()
        print(f"MRR: ${stats['mrr_usd']} | ARR: ${stats['arr_usd']} | Paying: {stats['active_subscribers']} | Trials: {stats['active_trials']}")
        print(f"Total Orgs: {stats['total_organizations']} | Total Users: {stats['total_users']} | Total Projects: {stats['total_projects']}")
        print(f"Tier Distribution: {stats['tier_distribution']}")

        assert stats["mrr_usd"] >= 299.0, f"Expected at least $299 MRR, got {stats['mrr_usd']}"
        assert stats["arr_usd"] >= 3588.0, f"Expected at least $3588 ARR, got {stats['arr_usd']}"
        assert stats["active_subscribers"] >= 1
        assert stats["active_trials"] >= 1
        assert stats["total_organizations"] >= 3

        # Phase 5: Strict RBAC Protection on Admin Console
        print_phase(5, "Strict RBAC Protection on Admin Console")
        # Invite viewer to Cyberdyne
        viewer_email = f"viewer_{uuid.uuid4().hex[:6]}@cyberdyne.corp"
        r_inv_v = client.post("/api/v1/org/members/invite", json={
            "email": viewer_email,
            "full_name": "Cyberdyne Viewer",
            "role": "viewer",
            "password": "Password123!"
        }, headers=cyber_headers)
        assert r_inv_v.status_code == 200

        r_v_login = client.post("/api/v1/auth/login", json={
            "email": viewer_email,
            "password": "Password123!"
        })
        assert r_v_login.status_code == 200
        viewer_headers = {"Authorization": f"Bearer {r_v_login.json()['access_token']}"}

        # Viewer attempt to access analytics -> 403 Forbidden
        r_hacked = client.get("/api/v1/admin/analytics", headers=viewer_headers)
        assert r_hacked.status_code == 403, f"Expected 403 Forbidden, got {r_hacked.status_code}"
        print("Non-owner access strictly rejected with 403 Forbidden.")

        # Phase 6: Multi-Tenant Platform Directory Retrieval
        print_phase(6, "Multi-Tenant Platform Directory Retrieval")
        r_dir = client.get("/api/v1/admin/organizations", headers=owner_headers)
        assert r_dir.status_code == 200, f"Directory failed: {r_dir.text}"
        directory = r_dir.json()
        print(f"Retrieved {len(directory)} customer organizations in platform directory.")
        assert len(directory) >= 3

        apex_entry = next((o for o in directory if o["id"] == apex_org_id), None)
        assert apex_entry is not None
        assert apex_entry["plan_tier"] == "enterprise"
        assert apex_entry["projects_count"] >= 1

        indie_entry = next((o for o in directory if o["id"] == indie_org_id), None)
        assert indie_entry is not None
        assert indie_entry["plan_tier"] == "community"

        # Phase 7: Administrative Plan Override & Trial Extension
        print_phase(7, "Administrative Plan Override & Trial Extension")
        # Override Indie Control Lab from Community to Enterprise with 30-day extended trial
        r_override = client.put(f"/api/v1/admin/organizations/{indie_org_id}/tier", json={
            "plan_tier": "enterprise",
            "extend_trial_days": 30
        }, headers=owner_headers)
        assert r_override.status_code == 200, f"Override failed: {r_override.text}"
        overridden = r_override.json()
        print(f"Overridden Org {indie_org_id} Tier: {overridden['plan_tier']}, Trial Days: {overridden['trial_days_remaining']}")
        assert overridden["plan_tier"] == "enterprise"
        assert overridden["is_trial"] is True
        assert overridden["trial_days_remaining"] >= 30

        # Verify from Indie customer's own perspective
        r_indie_sub = client.get("/api/v1/billing/subscription", headers=indie_headers)
        assert r_indie_sub.json()["plan_tier"] == "enterprise"
        print("Customer subscription endpoint immediately reflects administrative override.")

        # Phase 8: Feature Flags Catalog Inspection
        print_phase(8, "Enterprise Feature Flags Catalog Inspection")
        r_flags = client.get("/api/v1/admin/feature-flags", headers=owner_headers)
        assert r_flags.status_code == 200, f"Flags failed: {r_flags.text}"
        flags = r_flags.json()
        flag_keys = [f["key"] for f in flags]
        print(f"Found {len(flags)} enterprise feature flags: {flag_keys}")
        assert len(flags) >= 6
        assert "deep_semantic_validator" in flag_keys
        assert "ai_copilot_v2" in flag_keys
        assert "traceability_blast_radius" in flag_keys
        assert "enterprise_audit_stream" in flag_keys

        # Phase 9: Dynamic Feature Flag Lifecycle & Rollout Modification
        print_phase(9, "Feature Flag Lifecycle (Create -> Rollout Update -> Toggle -> Delete)")
        test_flag_key = f"quantum_opt_{uuid.uuid4().hex[:6]}"
        r_create_flag = client.post("/api/v1/admin/feature-flags", json={
            "key": test_flag_key,
            "name": "Quantum Automata Optimization",
            "description": "Sub-millisecond state compression kernel",
            "is_enabled": True,
            "minimum_tier": "enterprise",
            "allowed_org_ids": [indie_org_id],
            "rollout_percentage": 25
        }, headers=owner_headers)
        assert r_create_flag.status_code == 201
        created_flag = r_create_flag.json()
        flag_id = created_flag["id"]
        print(f"Created dynamic flag ID {flag_id}: {test_flag_key} (Rollout: {created_flag['rollout_percentage']}%)")

        # Update rollout to 100%
        r_upd_flag = client.put(f"/api/v1/admin/feature-flags/{flag_id}", json={
            "rollout_percentage": 100,
            "is_enabled": False
        }, headers=owner_headers)
        assert r_upd_flag.status_code == 200
        assert r_upd_flag.json()["rollout_percentage"] == 100
        assert r_upd_flag.json()["is_enabled"] is False
        print("Updated rollout to 100% and verified killswitch toggle.")

        # Delete flag
        r_del_flag = client.delete(f"/api/v1/admin/feature-flags/{flag_id}", headers=owner_headers)
        assert r_del_flag.status_code == 204
        print(f"Deleted flag {test_flag_key} (204 No Content).")

        # Phase 10: Client Runtime Feature Flag Evaluation
        print_phase(10, "Client Runtime Feature Flag Evaluation")
        r_eval = client.get("/api/v1/admin/feature-flags/eval", headers=owner_headers)
        assert r_eval.status_code == 200
        eval_map = r_eval.json()
        print(f"Client evaluated flags map: {eval_map}")
        # Enterprise user has access to enterprise and team flags
        assert eval_map.get("deep_semantic_validator") is True
        assert eval_map.get("traceability_blast_radius") is True
        assert eval_map.get("enterprise_audit_stream") is True

        print("\n=======================================================")
        print("ALL 10 PHASES OF PR 6 LIVE VERIFICATION PASSED WITH 100% SUCCESS!")
        print("=======================================================")

    finally:
        client.close()
        if server_process:
            print("Terminating test uvicorn server...")
            server_process.terminate()
            server_process.wait()

if __name__ == "__main__":
    main()

