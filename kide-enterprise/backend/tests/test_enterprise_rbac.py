import pytest
import uuid
from app.auth.rbac import UserRole, Permission

@pytest.mark.asyncio
async def test_cross_tenant_project_isolation(client):
    # 1. Register Org Alpha with Owner Alpha
    alpha_email = f"alpha_owner_{uuid.uuid4()}@alpha.corp"
    res_alpha = await client.post("/api/v1/auth/register", json={
        "email": alpha_email,
        "password": "Password123!",
        "full_name": "Alpha Owner",
        "org_name": "Alpha Industries"
    })
    assert res_alpha.status_code == 200
    alpha_token = res_alpha.json()["access_token"]
    alpha_headers = {"Authorization": f"Bearer {alpha_token}"}

    # 2. Register Org Beta with Owner Beta
    beta_email = f"beta_owner_{uuid.uuid4()}@beta.corp"
    res_beta = await client.post("/api/v1/auth/register", json={
        "email": beta_email,
        "password": "Password123!",
        "full_name": "Beta Owner",
        "org_name": "Beta Dynamics"
    })
    assert res_beta.status_code == 200
    beta_token = res_beta.json()["access_token"]
    beta_headers = {"Authorization": f"Bearer {beta_token}"}

    # 3. Create Project in Org Alpha
    create_proj_res = await client.post(
        "/api/v1/projects",
        json={"name": "Alpha Thermal Subsystem", "description": "Proprietary Alpha Design"},
        headers=alpha_headers
    )
    assert create_proj_res.status_code == 200
    alpha_project_id = create_proj_res.json()["id"]

    # 4. Create Project in Org Beta
    create_beta_proj = await client.post(
        "/api/v1/projects",
        json={"name": "Beta Propulsion Node", "description": "Proprietary Beta Design"},
        headers=beta_headers
    )
    assert create_beta_proj.status_code == 200
    beta_project_id = create_beta_proj.json()["id"]

    # 5. Verify Org Beta user CANNOT access Org Alpha project
    get_res = await client.get(f"/api/v1/projects/{alpha_project_id}", headers=beta_headers)
    assert get_res.status_code == 404

    # Org Beta user cannot read files of Org Alpha project
    files_res = await client.get(f"/api/v1/projects/{alpha_project_id}/files", headers=beta_headers)
    assert files_res.status_code == 404

    # Org Beta user cannot add file to Org Alpha project
    add_file_res = await client.post(
        f"/api/v1/projects/{alpha_project_id}/files",
        json={"filename": "exploit.mnc", "content": "malicious content"},
        headers=beta_headers
    )
    assert add_file_res.status_code == 404

    # Org Beta user cannot delete Org Alpha project
    del_res = await client.delete(f"/api/v1/projects/{alpha_project_id}", headers=beta_headers)
    assert del_res.status_code == 404

    # Org Beta user cannot access traceability matrix of Org Alpha project
    trace_res = await client.get(f"/api/v1/traceability/{alpha_project_id}/matrix", headers=beta_headers)
    assert trace_res.status_code == 404

    # 6. Listing projects returns only tenant-owned projects
    list_alpha = await client.get("/api/v1/projects", headers=alpha_headers)
    alpha_ids = [p["id"] for p in list_alpha.json()]
    assert alpha_project_id in alpha_ids
    assert beta_project_id not in alpha_ids

    list_beta = await client.get("/api/v1/projects", headers=beta_headers)
    beta_ids = [p["id"] for p in list_beta.json()]
    assert beta_project_id in beta_ids
    assert alpha_project_id not in beta_ids

@pytest.mark.asyncio
async def test_rbac_hierarchy_and_permissions(client):
    # 1. Setup Org with Owner
    owner_email = f"rbac_owner_{uuid.uuid4()}@cyberdyne.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Cyberdyne Owner",
        "org_name": "Cyberdyne Systems"
    })
    owner_headers = {"Authorization": f"Bearer {reg.json()['access_token']}"}

    # Create project
    proj_res = await client.post(
        "/api/v1/projects",
        json={"name": "T-800 Firmware", "description": "Chassis Controller"},
        headers=owner_headers
    )
    proj_id = proj_res.json()["id"]

    # 2. Owner invites a Viewer
    viewer_email = f"viewer_{uuid.uuid4()}@cyberdyne.corp"
    inv_viewer = await client.post(
        "/api/v1/org/members/invite",
        json={
            "email": viewer_email,
            "full_name": "Audit Viewer",
            "role": "viewer",
            "password": "ViewerPassword123!"
        },
        headers=owner_headers
    )
    assert inv_viewer.status_code == 200

    # 3. Owner invites an Engineer
    eng_email = f"engineer_{uuid.uuid4()}@cyberdyne.corp"
    inv_eng = await client.post(
        "/api/v1/org/members/invite",
        json={
            "email": eng_email,
            "full_name": "Chief Engineer",
            "role": "engineer",
            "password": "EngineerPassword123!"
        },
        headers=owner_headers
    )
    assert inv_eng.status_code == 200

    # 4. Login as Viewer
    login_viewer = await client.post("/api/v1/auth/login", json={
        "email": viewer_email,
        "password": "ViewerPassword123!"
    })
    viewer_headers = {"Authorization": f"Bearer {login_viewer.json()['access_token']}"}

    # Viewer CAN read project and files
    assert (await client.get(f"/api/v1/projects/{proj_id}", headers=viewer_headers)).status_code == 200
    assert (await client.get(f"/api/v1/projects/{proj_id}/files", headers=viewer_headers)).status_code == 200

    # Viewer CANNOT create project (403)
    cant_create_proj = await client.post(
        "/api/v1/projects",
        json={"name": "Disallowed Project"},
        headers=viewer_headers
    )
    assert cant_create_proj.status_code == 403

    # Viewer CANNOT add or mutate files (403)
    cant_add_file = await client.post(
        f"/api/v1/projects/{proj_id}/files",
        json={"filename": "test.dml", "content": "Package P {}"},
        headers=viewer_headers
    )
    assert cant_add_file.status_code == 403

    # Viewer CANNOT delete project (403)
    assert (await client.delete(f"/api/v1/projects/{proj_id}", headers=viewer_headers)).status_code == 403

    # Viewer CANNOT view audit logs (403)
    assert (await client.get("/api/v1/org/audit-logs", headers=viewer_headers)).status_code == 403

    # 5. Login as Engineer
    login_eng = await client.post("/api/v1/auth/login", json={
        "email": eng_email,
        "password": "EngineerPassword123!"
    })
    eng_headers = {"Authorization": f"Bearer {login_eng.json()['access_token']}"}

    # Engineer CAN add and mutate files
    add_file_res = await client.post(
        f"/api/v1/projects/{proj_id}/files",
        json={"filename": "Chassis.dml", "file_type": "dml", "content": "Package Robotics { DataModel Servo {} }"},
        headers=eng_headers
    )
    assert add_file_res.status_code == 200
    file_id = add_file_res.json()["id"]

    update_file_res = await client.put(
        f"/api/v1/projects/{proj_id}/files/{file_id}",
        json={"content": "Package Robotics { DataModel Servo { primitives { int pos = 0 } } }"},
        headers=eng_headers
    )
    assert update_file_res.status_code == 200

    # Engineer CANNOT delete the project (403)
    cant_del = await client.delete(f"/api/v1/projects/{proj_id}", headers=eng_headers)
    assert cant_del.status_code == 403

    # Engineer CANNOT invite members (403)
    cant_inv = await client.post(
        "/api/v1/org/members/invite",
        json={"email": "hacker@test.com", "full_name": "Hacker", "role": "admin"},
        headers=eng_headers
    )
    assert cant_inv.status_code == 403

@pytest.mark.asyncio
async def test_owner_and_admin_management_rules(client):
    owner_email = f"owner_mgmt_{uuid.uuid4()}@acme.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "Acme Owner",
        "org_name": "Acme Corp"
    })
    owner_headers = {"Authorization": f"Bearer {reg.json()['access_token']}"}

    # Owner invites Admin
    admin_email = f"admin_{uuid.uuid4()}@acme.corp"
    inv_admin = await client.post(
        "/api/v1/org/members/invite",
        json={
            "email": admin_email,
            "full_name": "Acme Admin",
            "role": "admin",
            "password": "AdminPassword123!"
        },
        headers=owner_headers
    )
    assert inv_admin.status_code == 200
    admin_user_id = inv_admin.json()["id"]

    # Login as Admin
    login_admin = await client.post("/api/v1/auth/login", json={
        "email": admin_email,
        "password": "AdminPassword123!"
    })
    admin_headers = {"Authorization": f"Bearer {login_admin.json()['access_token']}"}

    # Admin CAN invite an Engineer
    inv_eng = await client.post(
        "/api/v1/org/members/invite",
        json={
            "email": f"eng_{uuid.uuid4()}@acme.corp",
            "full_name": "Eng 1",
            "role": "engineer",
            "password": "EngPassword123!"
        },
        headers=admin_headers
    )
    assert inv_eng.status_code == 200

    # Admin CANNOT invite an Admin or Owner (403)
    inv_admin_fail = await client.post(
        "/api/v1/org/members/invite",
        json={
            "email": f"eng2_{uuid.uuid4()}@acme.corp",
            "full_name": "Eng 2",
            "role": "admin",
            "password": "EngPassword123!"
        },
        headers=admin_headers
    )
    assert inv_admin_fail.status_code == 403

    # Check Org Details
    details_res = await client.get("/api/v1/org/details", headers=admin_headers)
    assert details_res.status_code == 200
    assert details_res.json()["name"] == "Acme Corp"
    assert details_res.json()["members_count"] >= 3

    # Check Audit Logs
    audit_res = await client.get("/api/v1/org/audit-logs", headers=admin_headers)
    assert audit_res.status_code == 200
    actions = [entry["action"] for entry in audit_res.json()]
    assert "org:created" in actions
    assert "member:invite" in actions

@pytest.mark.asyncio
async def test_scoped_api_tokens_lifecycle(client):
    owner_email = f"tokens_owner_{uuid.uuid4()}@api.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": owner_email,
        "password": "Password123!",
        "full_name": "API Owner",
        "org_name": "API Systems"
    })
    owner_headers = {"Authorization": f"Bearer {reg.json()['access_token']}"}

    # Create a project
    proj_res = await client.post(
        "/api/v1/projects",
        json={"name": "Automated Pipeline Project"},
        headers=owner_headers
    )
    proj_id = proj_res.json()["id"]

    # 1. Create Scoped API Token
    create_tok_res = await client.post(
        "/api/v1/org/api-tokens",
        json={
            "name": "CI/CD Integration Runner",
            "scopes": ["read", "write"],
            "expires_days": 30
        },
        headers=owner_headers
    )
    assert create_tok_res.status_code == 200
    tok_data = create_tok_res.json()
    assert tok_data["name"] == "CI/CD Integration Runner"
    raw_token = tok_data["raw_token"]
    assert raw_token.startswith("kide_live_")
    token_id = tok_data["id"]

    # 2. Use API Token directly to authenticate requests
    api_token_headers = {"Authorization": f"Bearer {raw_token}"}
    projects_via_token = await client.get("/api/v1/projects", headers=api_token_headers)
    assert projects_via_token.status_code == 200
    assert any(p["id"] == proj_id for p in projects_via_token.json())

    # Add file via API Token
    add_file_via_token = await client.post(
        f"/api/v1/projects/{proj_id}/files",
        json={"filename": "TokenCreated.dml", "content": "Package P {}"},
        headers=api_token_headers
    )
    assert add_file_via_token.status_code == 200

    # 3. List active tokens
    list_tok_res = await client.get("/api/v1/org/api-tokens", headers=owner_headers)
    assert list_tok_res.status_code == 200
    assert any(t["id"] == token_id for t in list_tok_res.json())

    # 4. Revoke the token
    rev_res = await client.delete(f"/api/v1/org/api-tokens/{token_id}", headers=owner_headers)
    assert rev_res.status_code == 200

    # 5. Access with revoked token must now be REJECTED (401)
    revoked_call = await client.get("/api/v1/projects", headers=api_token_headers)
    assert revoked_call.status_code == 401

