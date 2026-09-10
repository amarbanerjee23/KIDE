import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_knowledge_catalog_and_import(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 1. Fetch catalog
    res = await client.get("/api/v1/knowledge/catalog", headers=headers)
    assert res.status_code == 200
    catalog = res.json()
    assert len(catalog) >= 5
    ids = [item["id"] for item in catalog]
    assert "boom_barrier_system" in ids
    assert "robotic_pick_and_place" in ids
    assert "industrial_cooling_system" in ids
    assert "chemical_batch_reactor" in ids
    assert "smart_meeting_room" in ids

    # 2. Fetch specific item
    res_item = await client.get("/api/v1/knowledge/catalog/boom_barrier_system", headers=headers)
    assert res_item.status_code == 200
    item = res_item.json()
    assert item["name"] == "Boom Barrier Vehicle Entry System"
    assert len(item["files"]) == 3

    # 3. Create a clean project
    res_proj = await client.post("/api/v1/projects/", json={"name": "KnowledgeTestProj", "description": "Test"}, headers=headers)
    assert res_proj.status_code == 200
    proj_id = res_proj.json()["id"]

    # 4. Import knowledge into project
    res_import = await client.post(f"/api/v1/projects/{proj_id}/import-knowledge", json={"catalog_id": "boom_barrier_system"}, headers=headers)
    assert res_import.status_code == 200
    data = res_import.json()
    assert len(data["imported_files"]) == 3

    # Verify project files exist via project files endpoint
    res_files = await client.get(f"/api/v1/projects/{proj_id}/files", headers=headers)
    assert res_files.status_code == 200
    filenames = [f["filename"] for f in res_files.json()]
    assert "BoomBarrier_DML.dml" in filenames
    assert "BoomBarrier_Cap.cap" in filenames
    assert "BoomBarrier_Ops.op" in filenames

async def test_custom_code_generators(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Create project
    res_proj = await client.post("/api/v1/projects/", json={"name": "GenTestProj"}, headers=headers)
    proj_id = res_proj.json()["id"]

    # 1. List generators (should include builtins)
    res_gens = await client.get(f"/api/v1/projects/{proj_id}/generators", headers=headers)
    assert res_gens.status_code == 200
    gens = res_gens.json()
    gen_ids = [g["id"] for g in gens]
    assert "python" in gen_ids
    assert "ros2" in gen_ids
    assert "java" in gen_ids
    assert "plc_st" in gen_ids
    assert "cpp" in gen_ids

    # 2. Run builtin PLC generator
    res_gen = await client.post(f"/api/v1/projects/{proj_id}/generate", json={"generator_id": "plc_st"}, headers=headers)
    assert res_gen.status_code == 200
    gen_data = res_gen.json()
    assert "GenTestProj_plc.st" in gen_data["files"]
    assert "FUNCTION_BLOCK FB_GenTestProj_Supervisor" in gen_data["files"]["GenTestProj_plc.st"]

    # 3. Create a project custom generator script
    custom_script = """def do_generate(model, fsa):
    fsa.generate_file("my_custom_output.txt", f"Hello from {model.get('name')}")
"""
    res_create_gen = await client.post(
        f"/api/v1/projects/{proj_id}/generators",
        json={"name": "custom_reporter", "code": custom_script},
        headers=headers
    )
    assert res_create_gen.status_code == 201

    # 4. Run the project custom generator
    res_custom_run = await client.post(
        f"/api/v1/projects/{proj_id}/generate",
        json={"generator_id": "project_custom_reporter.generator.py", "save_to_project": True},
        headers=headers
    )
    assert res_custom_run.status_code == 200
    assert "my_custom_output.txt" in res_custom_run.json()["files"]
    assert "Hello from GenTestProj" in res_custom_run.json()["files"]["my_custom_output.txt"]

async def test_simulation_runner(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    res_proj = await client.post("/api/v1/projects/", json={"name": "SimTestProj"}, headers=headers)
    proj_id = res_proj.json()["id"]

    # 1. Start simulation session
    res_sim = await client.post(f"/api/v1/projects/{proj_id}/simulate", json={"force_reset": True}, headers=headers)
    assert res_sim.status_code == 200
    sim_data = res_sim.json()
    assert sim_data["current_state"] == "INITIALIZED"
    assert "INIT" in sim_data["commands"]

    # 2. Execute command INIT
    res_cmd = await client.post(f"/api/v1/projects/{proj_id}/simulate/command", json={"command": "INIT"}, headers=headers)
    assert res_cmd.status_code == 200
    cmd_res = res_cmd.json()
    assert cmd_res["result"]["status"] == "SUCCESS"
    assert cmd_res["result"]["current_state"] == "READY"

    # 3. Inject event
    res_evt = await client.post(f"/api/v1/projects/{proj_id}/simulate/event", json={"event": "Started"}, headers=headers)
    assert res_evt.status_code == 200
    evt_res = res_evt.json()
    assert evt_res["result"]["event"] == "Started"

    # 4. Simulation step
    res_step = await client.post(f"/api/v1/projects/{proj_id}/simulate/step", headers=headers)
    assert res_step.status_code == 200
