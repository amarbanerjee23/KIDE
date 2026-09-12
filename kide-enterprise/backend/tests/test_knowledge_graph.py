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
    assert len(data["nodes"]) >= 15
    assert len(data["edges"]) >= 10
    stats = data["stats"]
    assert stats["domains"] >= 5
    assert stats["capabilities"] >= 5
    assert stats["devices"] >= 10

    # Ensure thesis case studies are represented
    node_names = [n["name"] for n in data["nodes"]]
    assert any("Boom Barrier" in name for name in node_names)
    assert any("Robotic" in name for name in node_names)
    assert any("Cooling" in name for name in node_names)

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

    # 3. Export to RDF Turtle
    res_ttl = await client.get(f"/api/v1/projects/{proj_id}/knowledge-graph/export?format=turtle", headers=headers)
    assert res_ttl.status_code == 200
    ttl_text = res_ttl.text
    assert "@prefix kide:" in ttl_text
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

