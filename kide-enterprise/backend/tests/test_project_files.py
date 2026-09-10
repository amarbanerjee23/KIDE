import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_templates_and_project_files(client: AsyncClient, test_user):
    token = test_user["access_token"]
    headers = {"Authorization": f"Bearer {token}"}
    
    # 1. List templates
    res = await client.get("/api/v1/projects/templates", headers=headers)
    assert res.status_code == 200
    templates = res.json()
    assert len(templates) >= 4
    template_keys = [t["key"] for t in templates]
    assert "industrial_cooling" in template_keys
    assert "pick_and_place" in template_keys
    assert "chemical_reactor" in template_keys
    assert "assembly_supervisor" in template_keys
    
    # 2. Create project from template
    res = await client.post("/api/v1/projects/from-template", json={
        "name": "Cooling System Test Project",
        "template": "industrial_cooling",
        "description": "Created from thesis template"
    }, headers=headers)
    assert res.status_code == 200
    proj = res.json()
    proj_id = proj["id"]
    assert proj["file_count"] == 5
    
    # 3. List files for project
    res_files = await client.get(f"/api/v1/projects/{proj_id}/files", headers=headers)
    assert res_files.status_code == 200
    files = res_files.json()
    assert len(files) == 5
    filenames = [f["filename"] for f in files]
    assert "CoolingSystem.activity" in filenames
    assert "CoolingSystem.json" in filenames
    
    # 4. Add a custom file
    res_add = await client.post(f"/api/v1/projects/{proj_id}/files", json={
        "filename": "CustomProcess.activity",
        "file_type": "activity",
        "content": "ActivityDiagram CustomProcess has activities { Activity Step1 {} }"
    }, headers=headers)
    assert res_add.status_code == 200
    added_file = res_add.json()
    file_id = added_file["id"]
    assert added_file["filename"] == "CustomProcess.activity"
    
    # 5. Update the file
    res_up = await client.put(f"/api/v1/projects/{proj_id}/files/{file_id}", json={
        "content": "ActivityDiagram CustomProcess has activities { Activity Step1 { time : 3.0 secs } }"
    }, headers=headers)
    assert res_up.status_code == 200
    updated_file = res_up.json()
    assert "3.0 secs" in updated_file["content"]
    assert updated_file["version"] == 2
    
    # 6. Test parse with language alias
    res_parse = await client.post("/api/v1/parse/activitydsl", json={
        "content": updated_file["content"]
    })
    assert res_parse.status_code == 200
    parse_data = res_parse.json()
    assert parse_data["ast"]["name"] == "CustomProcess"
    
    # 7. Test workspace transform with multiple files
    workspace_payload = {
        "workspace": {f["filename"]: f["content"] for f in files}
    }
    res_trans = await client.post("/api/v1/transform/", json=workspace_payload, headers=headers)
    assert res_trans.status_code == 200
    trans_data = res_trans.json()
    assert trans_data["model"]["name"] == "CoolingSystem"
