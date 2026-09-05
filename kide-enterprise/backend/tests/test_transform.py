import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_transform_basic_activity(client: AsyncClient, test_user):
    token = test_user["access_token"]
    payload = {
        "activity_diagram": {
            "name": "TestDiagram",
            "activities": [
                {
                    "name": "Act1",
                    "requires_operation": True,
                    "commands": [{"name": "CMD1"}],
                    "events": [{"name": "EVT1"}]
                }
            ]
        }
    }
    res = await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 200
    data = res.json()
    assert data["model"]["name"] == "TestDiagram"
    assert "Act1" in [a["name"] for a in data["model"]["control_node"]["actions"]]

async def test_transform_multi_activity(client: AsyncClient, test_user):
    token = test_user["access_token"]
    payload = {
        "activity_diagram": {
            "name": "TestDiagramMulti",
            "activities": [
                {"name": "Act1", "commands": [{"name": "C1"}]},
                {"name": "Act2", "commands": [{"name": "C2"}]},
                {"name": "Act3", "commands": [{"name": "C3"}]}
            ]
        }
    }
    res = await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 200
    data = res.json()
    actions = data["model"]["control_node"]["actions"]
    assert len(actions) == 3

async def test_transform_validation_error(client: AsyncClient, test_user):
    token = test_user["access_token"]
    payload = {
        "activity_diagram": {
            "name": "",
            "activities": []
        }
    }
    res = await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 400
    assert "errors" in res.json()["detail"]

async def test_transform_empty_activities(client: AsyncClient, test_user):
    token = test_user["access_token"]
    payload = {
        "activity_diagram": {
            "name": "EmptyDiagram",
            "activities": []
        }
    }
    res = await client.post("/api/v1/transform/", json=payload, headers={"Authorization": f"Bearer {token}"})
    assert res.status_code == 200
    data = res.json()
    # Check default lifecycle blocks
    cn = data["model"]["control_node"]
    cmd_names = [c["name"] for c in cn["command_response_block"]]
    assert "INIT" in cmd_names
