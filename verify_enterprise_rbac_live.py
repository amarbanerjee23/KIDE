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
    print("Starting PR 4: Multi-Tenancy Isolation, Enterprise RBAC & Security Audit Live Verification...")
    
    # Check if server is running on 8005; if not, spawn it
    server_process = None
    client = httpx.Client(base_url="http://127.0.0.1:8005", timeout=10.0)
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
        for _ in range(10):
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

        # Phase 2: Register Org 1 (Cyberdyne) & Org 2 (Weyland-Yutani)
        print_phase(2, "Register Tenants & Owners")
        cyberdyne_email = f"sarah_{uuid.uuid4().hex[:6]}@cyberdyne.corp"
        r_cyberdyne = client.post("/api/v1/auth/register", json={
            "email": cyberdyne_email,
            "password": "Password123!",
            "full_name": "Sarah Connor",
            "org_name": "Cyberdyne Systems"
        })
        assert r_cyberdyne.status_code == 200, f"Cyberdyne register failed: {r_cyberdyne.text}"
        cyberdyne_token = r_cyberdyne.json()["access_token"]
        cyberdyne_headers = {"Authorization": f"Bearer {cyberdyne_token}"}
        print(f"Registered Cyberdyne Systems Owner: {cyberdyne_email}")

        weyland_email = f"ripley_{uuid.uuid4().hex[:6]}@weyland.corp"
        r_weyland = client.post("/api/v1/auth/register", json={
            "email": weyland_email,
            "password": "Password123!",
            "full_name": "Ellen Ripley",
            "org_name": "Weyland-Yutani Corp"
        })
        assert r_weyland.status_code == 200, f"Weyland register failed: {r_weyland.text}"
        weyland_token = r_weyland.json()["access_token"]
        weyland_headers = {"Authorization": f"Bearer {weyland_token}"}
        print(f"Registered Weyland-Yutani Corp Owner: {weyland_email}")

        # Create Cyberdyne Project
        r_proj1 = client.post("/api/v1/projects", json={
            "name": "T-800 Cybernetic Controller",
            "description": "Proprietary Autonomous Node"
        }, headers=cyberdyne_headers)
        assert r_proj1.status_code == 200
        cyberdyne_proj_id = r_proj1.json()["id"]
        print(f"Created Cyberdyne Project ID: {cyberdyne_proj_id}")

        # Add proprietary file to Cyberdyne Project
        r_file1 = client.post(f"/api/v1/projects/{cyberdyne_proj_id}/files", json={
            "filename": "NeuralNet.dml",
            "file_type": "dml",
            "content": "Package Skynet { DataModel Synapse { primitives { int weight = 100 } } }"
        }, headers=cyberdyne_headers)
        assert r_file1.status_code == 200
        cyberdyne_file_id = r_file1.json()["id"]

        # Phase 3: Strict Cross-Tenant Isolation
        print_phase(3, "Cross-Tenant Isolation Enforcement")
        # Weyland Owner tries to read Cyberdyne project
        r_hack_get = client.get(f"/api/v1/projects/{cyberdyne_proj_id}", headers=weyland_headers)
        assert r_hack_get.status_code == 404, f"Cross-tenant get should return 404, got {r_hack_get.status_code}"
        print("Blocked cross-tenant project read (404 Not Found)")

        # Weyland Owner tries to list files in Cyberdyne project
        r_hack_files = client.get(f"/api/v1/projects/{cyberdyne_proj_id}/files", headers=weyland_headers)
        assert r_hack_files.status_code == 404, f"Cross-tenant files should return 404, got {r_hack_files.status_code}"
        print("Blocked cross-tenant file listing (404 Not Found)")

        # Weyland Owner tries to mutate file in Cyberdyne project
        r_hack_edit = client.put(
            f"/api/v1/projects/{cyberdyne_proj_id}/files/{cyberdyne_file_id}",
            json={"content": "Malicious payload"},
            headers=weyland_headers
        )
        assert r_hack_edit.status_code == 404, f"Cross-tenant edit should return 404, got {r_hack_edit.status_code}"
        print("Blocked cross-tenant file mutation (404 Not Found)")

        # Weyland Owner tries to delete Cyberdyne project
        r_hack_del = client.delete(f"/api/v1/projects/{cyberdyne_proj_id}", headers=weyland_headers)
        assert r_hack_del.status_code == 404, f"Cross-tenant delete should return 404, got {r_hack_del.status_code}"
        print("Blocked cross-tenant project deletion (404 Not Found)")

        # Phase 4: Team Member Invitations & RBAC Roles
        print_phase(4, "Team Member Invitations (Engineer & Viewer)")
        viewer_email = f"viewer_{uuid.uuid4().hex[:6]}@cyberdyne.corp"
        r_inv_v = client.post("/api/v1/org/members/invite", json={
            "email": viewer_email,
            "full_name": "Compliance Viewer",
            "role": "viewer",
            "password": "ViewerPassword123!"
        }, headers=cyberdyne_headers)
        assert r_inv_v.status_code == 200
        viewer_user_id = r_inv_v.json()["id"]
        print(f"Invited Viewer: {viewer_email} (ID: {viewer_user_id})")

        eng_email = f"engineer_{uuid.uuid4().hex[:6]}@cyberdyne.corp"
        r_inv_e = client.post("/api/v1/org/members/invite", json={
            "email": eng_email,
            "full_name": "Miles Dyson",
            "role": "engineer",
            "password": "EngineerPassword123!"
        }, headers=cyberdyne_headers)
        assert r_inv_e.status_code == 200
        eng_user_id = r_inv_e.json()["id"]
        print(f"Invited Engineer: {eng_email} (ID: {eng_user_id})")

        # Phase 5: Authenticate Viewer & Verify Read-Only Enforcements
        print_phase(5, "Viewer Role Permissions & Restriction Enforcements")
        r_log_v = client.post("/api/v1/auth/login", json={
            "email": viewer_email,
            "password": "ViewerPassword123!"
        })
        assert r_log_v.status_code == 200
        viewer_headers = {"Authorization": f"Bearer {r_log_v.json()['access_token']}"}

        # Viewer CAN read project and files
        r_v_read = client.get(f"/api/v1/projects/{cyberdyne_proj_id}", headers=viewer_headers)
        assert r_v_read.status_code == 200, "Viewer should be allowed to read project"
        print("Viewer successfully read tenant project (200 OK)")

        # Viewer CANNOT create project
        r_v_create = client.post("/api/v1/projects", json={"name": "Disallowed"}, headers=viewer_headers)
        assert r_v_create.status_code == 403, f"Viewer create should return 403, got {r_v_create.status_code}"
        print("Blocked Viewer from project creation (403 Forbidden)")

        # Viewer CANNOT edit file
        r_v_edit = client.post(
            f"/api/v1/projects/{cyberdyne_proj_id}/files",
            json={"filename": "test.dml", "content": "Package P {}"},
            headers=viewer_headers
        )
        assert r_v_edit.status_code == 403, f"Viewer file edit should return 403, got {r_v_edit.status_code}"
        print("Blocked Viewer from file editing (403 Forbidden)")

        # Viewer CANNOT view audit logs
        r_v_audit = client.get("/api/v1/org/audit-logs", headers=viewer_headers)
        assert r_v_audit.status_code == 403, f"Viewer audit logs should return 403, got {r_v_audit.status_code}"
        print("Blocked Viewer from audit logs (403 Forbidden)")

        # Phase 6: Authenticate Engineer & Verify Engineering Privileges
        print_phase(6, "Engineer Role Privileges & Administrative Boundaries")
        r_log_e = client.post("/api/v1/auth/login", json={
            "email": eng_email,
            "password": "EngineerPassword123!"
        })
        assert r_log_e.status_code == 200
        eng_headers = {"Authorization": f"Bearer {r_log_e.json()['access_token']}"}

        # Engineer CAN create/edit files
        r_e_file = client.post(
            f"/api/v1/projects/{cyberdyne_proj_id}/files",
            json={"filename": "CoolingAct.activity", "file_type": "activity", "content": "ActivityDiagram Cooling {}"},
            headers=eng_headers
        )
        assert r_e_file.status_code == 200
        print("Engineer successfully created project file (200 OK)")

        # Engineer CANNOT delete project
        r_e_del = client.delete(f"/api/v1/projects/{cyberdyne_proj_id}", headers=eng_headers)
        assert r_e_del.status_code == 403, f"Engineer delete project should return 403, got {r_e_del.status_code}"
        print("Blocked Engineer from deleting project (403 Forbidden)")

        # Engineer CANNOT invite members
        r_e_inv = client.post("/api/v1/org/members/invite", json={
            "email": "intruder@test.com", "full_name": "Intruder", "role": "admin"
        }, headers=eng_headers)
        assert r_e_inv.status_code == 403, f"Engineer invite should return 403, got {r_e_inv.status_code}"
        print("Blocked Engineer from inviting members (403 Forbidden)")

        # Phase 7: Role Elevation & Owner Protection
        print_phase(7, "Role Promotion & Sole Owner Protection Rules")
        # Owner updates Viewer to Engineer
        r_promote = client.put(
            f"/api/v1/org/members/{viewer_user_id}/role",
            json={"role": "engineer"},
            headers=cyberdyne_headers
        )
        assert r_promote.status_code == 200
        assert r_promote.json()["role"] == "engineer"
        print("Promoted Viewer to Engineer (200 OK)")

        # Owner attempts to demote self (sole owner) -> Must be rejected (400)
        r_me = client.get("/api/v1/auth/me", headers=cyberdyne_headers)
        owner_id = r_me.json()["id"]
        r_demote_self = client.put(
            f"/api/v1/org/members/{owner_id}/role",
            json={"role": "engineer"},
            headers=cyberdyne_headers
        )
        assert r_demote_self.status_code == 400, f"Demoting sole owner should return 400, got {r_demote_self.status_code}"
        print("Protected sole owner from demotion (400 Bad Request)")

        # Phase 8: Immutable Security Audit Log Verification
        print_phase(8, "Security Audit Trail Ledger Verification")
        r_audit = client.get("/api/v1/org/audit-logs", headers=cyberdyne_headers)
        assert r_audit.status_code == 200
        logs = r_audit.json()
        actions = [entry["action"] for entry in logs]
        print(f"Logged Audit Actions ({len(logs)} entries): {actions}")
        assert "org:created" in actions
        assert "user:login" in actions
        assert "member:invite" in actions
        assert "project:create" in actions
        assert "file:create" in actions
        assert "member:role_updated" in actions
        print("All security audit actions recorded in immutable ledger!")

        # Phase 9: Scoped Customer API Tokens Lifecycle
        print_phase(9, "Scoped Customer API Tokens (kide_live_...) Generation & Access")
        r_token = client.post("/api/v1/org/api-tokens", json={
            "name": "Nightly Build CI Runner",
            "scopes": ["read", "write"],
            "expires_days": 30
        }, headers=cyberdyne_headers)
        assert r_token.status_code == 200
        token_info = r_token.json()
        raw_api_token = token_info["raw_token"]
        api_token_id = token_info["id"]
        assert raw_api_token.startswith("kide_live_")
        print(f"Generated API Token: {token_info['prefix']} (ID: {api_token_id})")

        # Use API Token directly in Authorization header
        api_token_headers = {"Authorization": f"Bearer {raw_api_token}"}
        r_tok_projects = client.get("/api/v1/projects", headers=api_token_headers)
        assert r_tok_projects.status_code == 200
        print("Successfully authenticated and read projects via Scoped API Token (200 OK)")

        r_tok_file = client.post(
            f"/api/v1/projects/{cyberdyne_proj_id}/files",
            json={"filename": "Telemetry.dml", "file_type": "dml", "content": "Package Telemetry {}"},
            headers=api_token_headers
        )
        assert r_tok_file.status_code == 200
        print("Successfully created project file via Scoped API Token (200 OK)")

        # Phase 10: API Token Revocation & Invalidation
        print_phase(10, "API Token Revocation & Immediate Invalidation")
        r_rev = client.delete(f"/api/v1/org/api-tokens/{api_token_id}", headers=cyberdyne_headers)
        assert r_rev.status_code == 200
        print(f"Revoked API Token ID: {api_token_id}")

        # Request with revoked token must be rejected (401 Unauthorized)
        r_tok_revoked = client.get("/api/v1/projects", headers=api_token_headers)
        assert r_tok_revoked.status_code == 401, f"Revoked token call should return 401, got {r_tok_revoked.status_code}"
        print("Subsequent call with revoked token immediately rejected (401 Unauthorized)")

        print("\n=======================================================")
        print("PR 4 LIVE VERIFICATION COMPLETE: ALL 10 PHASES PASSED 100%!")
        print("=======================================================")

    finally:
        client.close()
        if server_process:
            print("Terminating background uvicorn server...")
            server_process.terminate()
            server_process.wait()

if __name__ == "__main__":
    main()

