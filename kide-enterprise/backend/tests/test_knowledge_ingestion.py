import pytest
import uuid
import json

@pytest.mark.asyncio
async def test_knowledge_ingestion_lifecycle(client):
    # 1. Register engineer
    engineer_email = f"eng_{uuid.uuid4().hex[:6]}@automation.corp"
    reg_res = await client.post("/api/v1/auth/register", json={
        "email": engineer_email,
        "password": "Password123!",
        "full_name": "Automation Lead Engineer",
        "org_name": "Automation Solutions Inc"
    })
    assert reg_res.status_code == 200
    token = reg_res.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 2. Query available sample templates
    tpl_res = await client.get("/api/v1/knowledge/ingest/templates", headers=headers)
    assert tpl_res.status_code == 200
    templates = tpl_res.json()
    assert len(templates) >= 5
    sample_ids = [t["id"] for t in templates]
    assert "honeywell_st700" in sample_ids
    assert "emerson_dvc6200" in sample_ids
    assert "schneider_atv320" in sample_ids
    assert "keyence_iv3" in sample_ids
    assert "endress_promass" in sample_ids

    # 3. Ingest a sample datasheet (Honeywell ST700)
    ingest_res = await client.post(
        "/api/v1/knowledge/ingest/sample",
        json={"sample_id": "honeywell_st700"},
        headers=headers
    )
    assert ingest_res.status_code == 201
    job = ingest_res.json()
    job_id = job["id"]
    assert job["status"] == "completed"
    assert job["progress_pct"] == 100
    assert job["extracted_devices_count"] >= 1
    assert job["extracted_parameters_count"] >= 3

    # 4. Fetch the staged artifacts for this job
    staged_res = await client.get(f"/api/v1/knowledge/ingest/jobs/{job_id}/staged", headers=headers)
    assert staged_res.status_code == 200
    staged_list = staged_res.json()
    assert len(staged_list) >= 1
    artifact = staged_list[0]
    artifact_id = artifact["id"]
    assert artifact["status"] == "draft"
    assert "Honeywell" in artifact["name"]
    assert len(artifact["generated_dml"]) > 0
    assert len(artifact["generated_mnc"]) > 0
    assert len(artifact["generated_cap"]) > 0
    assert len(artifact["generated_op"]) > 0
    assert len(artifact["extracted_datapoints"]) >= 3

    # 5. Approve and promote staged artifact into Knowledge Hub
    approve_res = await client.post(
        f"/api/v1/knowledge/ingest/staged/{artifact_id}/approve",
        json={"notes": "Approved for plant-wide commissioning"},
        headers=headers
    )
    assert approve_res.status_code == 200
    approved_artifact = approve_res.json()
    assert approved_artifact["status"] == "approved"
    assert approved_artifact["review_notes"] == "Approved for plant-wide commissioning"

    # 6. Verify the promoted item is now accessible in global catalog
    catalog_res = await client.get("/api/v1/knowledge/catalog", headers=headers)
    assert catalog_res.status_code == 200
    catalog = catalog_res.json()
    catalog_names = [c["name"] for c in catalog]
    assert any("Honeywell" in name for name in catalog_names)

    # 7. Create a project and import the approved staged artifact
    proj_res = await client.post("/api/v1/projects/", json={
        "name": "Chemical Skid Unit 4",
        "description": "Reactor feed control system"
    }, headers=headers)
    assert proj_res.status_code == 200
    project_id = proj_res.json()["id"]

    import_res = await client.post(
        f"/api/v1/knowledge/ingest/staged/{artifact_id}/import-to-project/{project_id}",
        headers=headers
    )
    assert import_res.status_code == 200
    import_data = import_res.json()
    assert len(import_data["imported_files"]) >= 3

    # Verify project files exist
    files_res = await client.get(f"/api/v1/projects/{project_id}/files", headers=headers)
    assert files_res.status_code == 200
    files = files_res.json()
    file_names = [f["filename"] for f in files]
    assert any("DML.dml" in f for f in file_names)
    assert any("MNC.mnc" in f for f in file_names)
    assert any("Cap.cap" in f for f in file_names)

@pytest.mark.asyncio
async def test_csv_and_json_ingestion_and_rejection(client):
    # Register user
    email = f"csv_eng_{uuid.uuid4().hex[:6]}@test.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": email,
        "password": "Password123!",
        "full_name": "CSV Ingestion Tester",
        "org_name": "CSV Test Corp"
    })
    token = reg.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 1. Submit raw CSV register map
    csv_content = """Register_Name,Type,Unit,Min,Max,Description
Motor_Speed,float,rpm,0,3000,Drive shaft rotational speed
Stator_Current,float,A,0,25,Output current in Amperes
DC_Bus_Volts,float,V,0,750,Internal DC intermediate link voltage
Fault_Word,int,code,0,65535,Modbus drive status error bitfield
"""
    csv_job_res = await client.post("/api/v1/knowledge/ingest/submit-raw", json={
        "filename": "ATV_Drive_Registers.csv",
        "file_type": "csv",
        "content": csv_content
    }, headers=headers)
    assert csv_job_res.status_code == 201
    job = csv_job_res.json()
    assert job["status"] == "completed"
    assert job["extracted_parameters_count"] == 4

    # 2. Check staged artifact
    staged_res = await client.get(f"/api/v1/knowledge/ingest/jobs/{job['id']}/staged", headers=headers)
    assert staged_res.status_code == 200
    artifacts = staged_res.json()
    assert len(artifacts) == 1
    artifact = artifacts[0]
    assert artifact["status"] == "draft"
    assert len(artifact["extracted_datapoints"]) == 4

    # 3. Reject the staged artifact
    reject_res = await client.post(
        f"/api/v1/knowledge/ingest/staged/{artifact['id']}/reject",
        json={"notes": "Superseded by firmware v3.2 specification"},
        headers=headers
    )
    assert reject_res.status_code == 200
    rejected = reject_res.json()
    assert rejected["status"] == "rejected"
    assert rejected["review_notes"] == "Superseded by firmware v3.2 specification"

@pytest.mark.asyncio
async def test_notifications_service_and_stats(client):
    email = f"notif_user_{uuid.uuid4().hex[:6]}@notif.corp"
    reg = await client.post("/api/v1/auth/register", json={
        "email": email,
        "password": "Password123!",
        "full_name": "Notification Observer",
        "org_name": "Notif Observability Inc"
    })
    token = reg.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Ingest a sample to trigger notifications
    await client.post(
        "/api/v1/knowledge/ingest/sample",
        json={"sample_id": "keyence_iv3"},
        headers=headers
    )

    # Check notifications list
    notif_res = await client.get("/api/v1/notifications", headers=headers)
    assert notif_res.status_code == 200
    notifs = notif_res.json()
    assert len(notifs) >= 1
    assert any(n["notification_type"] == "ingestion_completed" for n in notifs)

    # Check notification stats
    stats_res = await client.get("/api/v1/notifications/stats", headers=headers)
    assert stats_res.status_code == 200
    stats = stats_res.json()
    assert stats["total_notifications"] >= 1
    assert stats["sent_count"] >= 1

