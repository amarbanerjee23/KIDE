import pytest

@pytest.mark.asyncio
async def test_ai_providers_endpoint(client, test_user):
    headers = {"Authorization": f"Bearer {test_user['access_token']}"}
    resp = await client.get("/api/v1/ai/providers", headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert "available_providers" in data
    assert "active_provider" in data
    assert "active_model" in data
    assert "deterministic" in data["available_providers"]

@pytest.mark.asyncio
async def test_ai_chat_and_provenance_flow(client, test_user):
    headers = {"Authorization": f"Bearer {test_user['access_token']}"}

    # 1. Create a project from reference cooling template
    proj_resp = await client.post(
        "/api/v1/projects/from-template",
        headers=headers,
        json={"name": "AI_Supervised_Cooling", "template": "industrial_cooling"}
    )
    assert proj_resp.status_code == 200
    project = proj_resp.json()
    proj_id = project["id"]

    # 2. Test chat with validation tool execution
    chat_resp = await client.post(
        "/api/v1/ai/chat",
        headers=headers,
        json={
            "project_id": proj_id,
            "messages": [{"role": "user", "content": "Please validate my project models"}],
            "context_file": "CoolingSystem.activity"
        }
    )
    if chat_resp.status_code != 200:
        print("CHAT RESP ERROR:", chat_resp.status_code, chat_resp.text)
    assert chat_resp.status_code == 200
    chat_data = chat_resp.json()
    assert chat_data["provider"] in ("gemini", "openai", "deterministic")
    assert len(chat_data["tool_calls"]) >= 1
    tool_names = [t["tool"] for t in chat_data["tool_calls"]]
    assert "validate_project" in tool_names
    assert "✓ **Project Validation Passed**" in chat_data["message"]
    prov_id = chat_data["provenance_id"]
    assert prov_id is not None

    # 3. Test chat with automata synthesis tool execution
    synth_resp = await client.post(
        "/api/v1/ai/chat",
        headers=headers,
        json={
            "project_id": proj_id,
            "messages": [{"role": "user", "content": "Synthesize the supervisory state machine"}],
            "context_file": "CoolingSystem.activity"
        }
    )
    assert synth_resp.status_code == 200
    synth_data = synth_resp.json()
    tool_names = [t["tool"] for t in synth_data["tool_calls"]]
    assert "synthesize_automata" in tool_names
    assert "Supervisory Automata Synthesized" in synth_data["message"]

    # 4. Test chat with knowledge search tool execution
    kg_resp = await client.post(
        "/api/v1/ai/chat",
        headers=headers,
        json={
            "project_id": proj_id,
            "messages": [{"role": "user", "content": "Search knowledge for barrier RFID gate"}]
        }
    )
    if kg_resp.status_code != 200:
        print("KG ERROR:", kg_resp.status_code, kg_resp.text)
    assert kg_resp.status_code == 200
    kg_data = kg_resp.json()
    tool_names = [t["tool"] for t in kg_data["tool_calls"]]
    assert "search_knowledge" in tool_names
    assert "Knowledge Hub" in kg_data["message"]

    # 5. Test patch proposal and application
    patch_chat_resp = await client.post(
        "/api/v1/ai/chat",
        headers=headers,
        json={
            "project_id": proj_id,
            "messages": [{"role": "user", "content": "Create a safety gate barrier activity patch"}],
            "context_file": "GateSafety.activity"
        }
    )
    assert patch_chat_resp.status_code == 200
    patch_chat_data = patch_chat_resp.json()
    assert len(patch_chat_data["proposed_patches"]) >= 1
    patch = patch_chat_data["proposed_patches"][0]
    assert patch["filename"] == "GateSafety.activity"
    assert "diff" in patch
    assert len(patch["new_content"]) > 0

    # 6. Apply patch
    apply_resp = await client.post(
        "/api/v1/ai/apply-patch",
        headers=headers,
        json={
            "project_id": proj_id,
            "provenance_id": patch_chat_data["provenance_id"],
            "patches": [patch]
        }
    )
    assert apply_resp.status_code == 200
    apply_data = apply_resp.json()
    assert apply_data["status"] == "applied"
    assert any("GateSafety.activity" in f for f in apply_data["applied_files"])

    # 7. Verify file exists in project files
    files_resp = await client.get(f"/api/v1/projects/{proj_id}/files", headers=headers)
    assert files_resp.status_code == 200
    project_files = files_resp.json()
    file_names = [f["filename"] for f in project_files]
    assert "GateSafety.activity" in file_names

    # 8. Verify provenance audit trail
    prov_resp = await client.get(f"/api/v1/ai/provenance/{proj_id}", headers=headers)
    assert prov_resp.status_code == 200
    prov_records = prov_resp.json()
    assert len(prov_records) >= 4
    # The applied patch record should have status 'applied'
    applied_record = next((r for r in prov_records if r["id"] == patch_chat_data["provenance_id"]), None)
    assert applied_record is not None
    assert applied_record["status"] == "applied"
