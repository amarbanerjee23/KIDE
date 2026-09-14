import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_global_knowledge_graph(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    res = await client.get("/api/v1/knowledge/graph", headers=headers)
    assert res.status_code == 200
    data = res.json()
    assert data["scope"] == "global"
    assert len(data["nodes"]) >= 30
    assert len(data["edges"]) >= 20
    stats = data["stats"]
    assert stats["domains"] >= 7
    assert stats["capabilities"] >= 7
    assert stats["devices"] >= 15
    assert stats["workflows"] >= 5
    assert stats["activities"] >= 15
    assert stats["operations"] >= 7
    assert stats["data_models"] >= 7
    assert stats["commands"] >= 10
    assert stats["events"] >= 10
    assert stats["alarms"] >= 10
    assert stats["data_points"] >= 10
    assert stats["parameters"] >= 10

    # Ensure fine-grained ontology types exist
    all_types = {n["type"] for n in data["nodes"]}
    assert "command" in all_types
    assert "event" in all_types
    assert "alarm" in all_types
    assert "datapoint" in all_types
    assert "parameter" in all_types

    # Ensure all 7 thesis case studies and domains are represented
    node_names = [n["name"] for n in data["nodes"]]
    assert any("Boom Barrier" in name for name in node_names)
    assert any("Robotic" in name for name in node_names)
    assert any("Cooling" in name for name in node_names)
    assert any("Chemical" in name or "Reactor" in name for name in node_names)
    assert any("Meeting" in name or "Room" in name for name in node_names)
    assert any("Assembly" in name or "Automotive" in name for name in node_names)
    assert any("Water" in name or "Purification" in name for name in node_names)

async def test_knowledge_store_summary_and_entities(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 1. Summary
    res_sum = await client.get("/api/v1/knowledge/store/summary", headers=headers)
    assert res_sum.status_code == 200
    summary = res_sum.json()
    assert "stats" in summary
    assert "domains" in summary
    assert len(summary["domains"]) >= 7

    # 2. Query entities by type: capability
    res_caps = await client.get("/api/v1/knowledge/store/entities?entity_type=capability", headers=headers)
    assert res_caps.status_code == 200
    caps = res_caps.json()
    assert len(caps) >= 7
    assert all(c["type"] == "capability" for c in caps)

    # 3. Query entities by type: activity
    res_acts = await client.get("/api/v1/knowledge/store/entities?entity_type=activity", headers=headers)
    assert res_acts.status_code == 200
    acts = res_acts.json()
    assert len(acts) >= 15
    assert all(a["type"] == "activity" for a in acts)

    # 4. Query entities by keyword search
    res_search = await client.get("/api/v1/knowledge/store/entities?query=barrier", headers=headers)
    assert res_search.status_code == 200
    search_res = res_search.json()
    assert len(search_res) > 0
    assert any("BoomBarrier" in item["name"] for item in search_res)

    # 5. Entity Detail
    target_cap = caps[0]
    res_detail = await client.get(f"/api/v1/knowledge/store/entities/{target_cap['id']}", headers=headers)
    assert res_detail.status_code == 200
    detail = res_detail.json()
    assert detail["entity"]["name"] == target_cap["name"]
    assert detail["declaring_file"] is not None
    assert "content" in detail["declaring_file"]

async def test_project_single_entity_import(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 1. Create a blank project
    res_proj = await client.post("/api/v1/projects", json={
        "name": "SingleEntityImportTest",
        "description": "Testing selective capability and activity import"
    }, headers=headers)
    assert res_proj.status_code == 200
    proj_id = res_proj.json()["id"]

    # 2. Get a capability entity ID
    res_caps = await client.get("/api/v1/knowledge/store/entities?entity_type=capability", headers=headers)
    assert res_caps.status_code == 200
    caps = res_caps.json()
    cap_entity = next(c for c in caps if "BoomBarrierCap" in c["name"])

    # 3. Import this single entity into project
    res_import = await client.post(f"/api/v1/projects/{proj_id}/import-entity", json={
        "entity_id": cap_entity["id"]
    }, headers=headers)
    assert res_import.status_code == 200
    import_data = res_import.json()
    assert "imported_file" in import_data
    assert "BoomBarrier_Cap.cap" in import_data["imported_file"]["filename"]

    # 4. Verify project files list now contains the imported file
    res_files = await client.get(f"/api/v1/projects/{proj_id}/files", headers=headers)
    assert res_files.status_code == 200
    files = res_files.json()
    assert any(f["filename"] == "BoomBarrier_Cap.cap" for f in files)

async def test_project_knowledge_graph_and_export(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 1. Create project from thesis template
    res_proj = await client.post("/api/v1/projects/from-template", json={
        "name": "GraphTestCooling",
        "template": "industrial_cooling"
    }, headers=headers)
    assert res_proj.status_code == 200
    proj_id = res_proj.json()["id"]

    # 2. Get project knowledge graph
    res_graph = await client.get(f"/api/v1/projects/{proj_id}/knowledge-graph", headers=headers)
    assert res_graph.status_code == 200
    graph = res_graph.json()
    assert graph["scope"] == "project"
    assert graph["project_id"] == proj_id
    assert len(graph["nodes"]) >= 5
    assert len(graph["edges"]) >= 4

    types = {n["type"] for n in graph["nodes"]}
    assert "datamodel" in types
    assert "capability" in types
    assert "activity" in types
    assert "operating_state" in types
    assert "command" in types
    assert "event" in types
    assert "parameter" in types

    # 3. Export to RDF Turtle
    res_ttl = await client.get(f"/api/v1/projects/{proj_id}/knowledge-graph/export?format=turtle", headers=headers)
    assert res_ttl.status_code == 200
    ttl_text = res_ttl.text
    assert "@prefix kide:" in ttl_text
    assert "@prefix owl: <http://www.w3.org/2002/07/owl#>" in ttl_text
    assert "kide:Command rdf:type owl:Class" in ttl_text
    assert "kide:offersCommand rdf:type owl:ObjectProperty" in ttl_text
    assert "rdf:type" in ttl_text

async def test_capability_matching(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Match temperature capability
    res_match = await client.post("/api/v1/knowledge/match", json={"query": "chiller temperature sensor"}, headers=headers)
    assert res_match.status_code == 200
    matches = res_match.json()
    assert len(matches) > 0
    top_match = matches[0]
    assert "Cooling" in top_match["system_name"] or "Chiller" in top_match["system_name"]
    assert top_match["score"] > 50
