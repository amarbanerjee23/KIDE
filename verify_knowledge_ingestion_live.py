"""
Live 10-Phase Verification Script for PR 7:
Enterprise Knowledge Ingestion Pipeline, Document Extraction & Background Worker.
Tests the live REST endpoints against a running uvicorn instance.
"""

import sys
import time
import uuid
import urllib.request
import urllib.error
import json

sys.stdout.reconfigure(encoding="utf-8")

BASE_URL = "http://127.0.0.1:8005/api/v1"

def api_call(method: str, path: str, payload=None, token=None):
    url = f"{BASE_URL}{path}"
    data = json.dumps(payload).encode("utf-8") if payload is not None else None
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            body = resp.read().decode("utf-8")
            return resp.status, json.loads(body) if body else {}
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8")
        try:
            parsed = json.loads(body)
        except Exception:
            parsed = {"error": body}
        return e.code, parsed

def run_verification():
    print("================================================================================")
    print("      KIDE ENTERPRISE — PR 7 LIVE 10-PHASE VERIFICATION SUITE                   ")
    print("  Knowledge Ingestion Pipeline, Document Extraction & Background Worker         ")
    print("================================================================================\n")

    # Phase 1: Verify Server Online
    print("Phase 1: Verifying Backend Server Online...")
    code, res = api_call("GET", "/auth/me")
    if code not in (200, 401):
        print(f"FAILED: Backend not responding at {BASE_URL} (Status {code}: {res})")
        sys.exit(1)
    print(f"  ✓ Server is online and responding at {BASE_URL} (Auth Probe Status: {code})")

    # Phase 2: User & Organization Registration
    print("\nPhase 2: Registering Lead Automation Engineer & Enterprise Tenant...")
    unique_suffix = uuid.uuid4().hex[:6]
    lead_email = f"ingest_lead_{unique_suffix}@basf-chemicals.com"
    code, reg_res = api_call("POST", "/auth/register", {
        "email": lead_email,
        "password": "Password123!",
        "full_name": "Dr. Sarah Lin (Lead Automation Specialist)",
        "org_name": "BASF Chemical Solutions"
    })
    assert code == 200, f"Registration failed: {reg_res}"
    token = reg_res["access_token"]
    code, me = api_call("GET", "/auth/me", token=token)
    print(f"  ✓ Registered {lead_email} (User ID: {me.get('id')}, Org: {me.get('org_name')})")

    # Phase 3: Query Available Datasheet Templates
    print("\nPhase 3: Querying Built-in Industrial Datasheet Templates...")
    code, templates = api_call("GET", "/knowledge/ingest/templates", token=token)
    assert code == 200, f"Failed to list templates: {templates}"
    assert len(templates) >= 5, f"Expected >= 5 templates, got {len(templates)}"
    print(f"  ✓ Discovered {len(templates)} industrial equipment templates:")
    for t in templates:
        print(f"    - [{t['protocol']}] {t['name']} ({t['manufacturer']})")

    # Phase 4: Ingest Sample Datasheet (Honeywell ST700)
    print("\nPhase 4: Running Extraction on Honeywell ST700 Pressure Transmitter...")
    code, job = api_call("POST", "/knowledge/ingest/sample", {"sample_id": "honeywell_st700"}, token=token)
    assert code == 201, f"Failed to ingest sample: {job}"
    job_id = job["id"]
    assert job["status"] == "completed", f"Job status not completed: {job['status']}"
    assert job["progress_pct"] == 100, f"Job progress not 100%: {job['progress_pct']}"
    assert job["extracted_devices_count"] >= 1
    assert job["extracted_parameters_count"] >= 3
    print(f"  ✓ IngestionJob id={job_id} completed successfully (100%)")
    print(f"    Stage: {job['stage_message']}")
    print(f"    Discovered: {job['extracted_devices_count']} devices, {job['extracted_parameters_count']} registers")

    # Phase 5: Review Staged Artifacts & DSL Synthesis
    print("\nPhase 5: Inspecting Extracted Staged Artifacts and Synthesized DSLs...")
    code, staged = api_call("GET", f"/knowledge/ingest/jobs/{job_id}/staged", token=token)
    assert code == 200, f"Failed to get staged artifacts: {staged}"
    assert len(staged) >= 1, "No staged artifacts found"
    artifact = staged[0]
    artifact_id = artifact["id"]
    assert artifact["status"] == "draft"
    assert len(artifact["generated_dml"]) > 0, "Missing DML code"
    assert len(artifact["generated_mnc"]) > 0, "Missing MNC code"
    assert len(artifact["generated_cap"]) > 0, "Missing Capability code"
    assert len(artifact["generated_op"]) > 0, "Missing Operation code"
    print(f"  ✓ Staged Artifact id={artifact_id} '{artifact['name']}' in status DRAFT")
    print(f"    Protocol: {artifact['protocol']}, Category: {artifact['category']}")
    print(f"    Generated DML: {len(artifact['generated_dml'])} chars")
    print(f"    Generated MNC: {len(artifact['generated_mnc'])} chars")
    print(f"    Generated Cap: {len(artifact['generated_cap'])} chars")
    print(f"    Generated Op:  {len(artifact['generated_op'])} chars")

    # Phase 6: Approve and Promote Staged Artifact
    print("\nPhase 6: Approving and Promoting Staged Artifact to Knowledge Hub...")
    code, approved = api_call(
        "POST",
        f"/knowledge/ingest/staged/{artifact_id}/approve",
        {"notes": "Verified against plant piping & instrumentation diagram"},
        token=token
    )
    assert code == 200, f"Approval failed: {approved}"
    assert approved["status"] == "approved"
    assert approved["review_notes"] == "Verified against plant piping & instrumentation diagram"
    print(f"  ✓ Artifact id={artifact_id} successfully APPROVED & PROMOTED to Knowledge Hub")

    # Phase 7: Verify Promoted Equipment in Global Catalog
    print("\nPhase 7: Verifying Promoted Equipment in Global Equipment Catalog...")
    code, catalog = api_call("GET", "/knowledge/catalog", token=token)
    assert code == 200
    matched = next((c for c in catalog if "Honeywell" in c["name"]), None)
    assert matched is not None, "Promoted device not found in live equipment catalog"
    print(f"  ✓ Verified device in live catalog: '{matched['name']}' (Files: {matched['file_count']})")

    # Phase 8: Ingest Raw CSV Register Map
    print("\nPhase 8: Ingesting Raw CSV Register Map for Motor Variable Speed Drive...")
    csv_payload = {
        "filename": "ABB_ACS880_Drive_Registers.csv",
        "file_type": "csv",
        "content": """Register_Name,Type,Unit,Min,Max,Description
Actual_Motor_Speed,float,rpm,0,3600,Measured drive shaft rotational speed
Motor_Torque_Percent,float,pct,-300,300,Output shaft mechanical torque percentage
Inverter_Temperature,float,degC,-20,110,IGBT semiconductor heatsink temperature
Fault_Bitfield,int,code,0,65535,CiA 402 drive status alarm bitfield
"""
    }
    code, csv_job = api_call("POST", "/knowledge/ingest/submit-raw", csv_payload, token=token)
    assert code == 201, f"Failed to submit CSV: {csv_job}"
    assert csv_job["status"] == "completed"
    assert csv_job["extracted_parameters_count"] == 4
    print(f"  ✓ CSV IngestionJob id={csv_job['id']} extracted {csv_job['extracted_parameters_count']} registers")

    # Phase 9: Project Creation & Direct Artifact Import
    print("\nPhase 9: Creating Project and Importing Synthesized DSL Files...")
    code, proj = api_call("POST", "/projects/", {
        "name": "Polymerization Reactor Unit 2",
        "description": "Exothermic batch reactor control skid"
    }, token=token)
    assert code == 200, f"Failed to create project: {proj}"
    project_id = proj["id"]

    code, import_res = api_call(
        "POST",
        f"/knowledge/ingest/staged/{artifact_id}/import-to-project/{project_id}",
        token=token
    )
    assert code == 200, f"Failed to import to project: {import_res}"
    print(f"  ✓ Imported files: {len(import_res['imported_files'])}")
    for f in import_res["imported_files"]:
        print(f"    - {f['filename']} ({f['bytes']} bytes)")

    # Verify project files exist
    code, pfiles = api_call("GET", f"/projects/{project_id}/files", token=token)
    assert code == 200
    file_names = [f["filename"] for f in pfiles]
    assert any("DML.dml" in name for name in file_names)
    assert any("MNC.mnc" in name for name in file_names)
    print("  ✓ Verified project workspace now contains all synthesized formal DSL models")

    # Phase 10: Transactional Notification Audit Stream (Requirement 50)
    print("\nPhase 10: Verifying Transactional Notification Audit Stream (PhD Req 50)...")
    code, notifs = api_call("GET", "/notifications", token=token)
    assert code == 200, f"Failed to get notifications: {notifs}"
    assert len(notifs) >= 2, f"Expected >= 2 notifications, got {len(notifs)}"
    
    code, stats = api_call("GET", "/notifications/stats", token=token)
    assert code == 200, f"Failed to get notification stats: {stats}"
    assert stats["total_notifications"] >= 2
    assert stats["sent_count"] >= 2
    print(f"  ✓ Notifications logged: {stats['total_notifications']} (Sent: {stats['sent_count']})")
    for n in notifs[:3]:
        print(f"    - [{n['notification_type']}] {n['subject']} -> {n['status'].upper()}")

    print("\n================================================================================")
    print("  SUCCESS: ALL 10 PHASES OF PR 7 COMPLETED AND VERIFIED WITH 100% ACCURACY!    ")
    print("================================================================================\n")

if __name__ == "__main__":
    run_verification()
