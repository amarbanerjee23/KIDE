import pytest
from httpx import AsyncClient

pytestmark = pytest.mark.asyncio

async def test_register_and_login(client: AsyncClient):
    res1 = await client.post("/api/v1/auth/register", json={
        "email": "new@example.com",
        "password": "pass",
        "full_name": "New",
        "org_name": "Org"
    })
    assert res1.status_code == 200
    assert "access_token" in res1.json()

    res2 = await client.post("/api/v1/auth/login", json={
        "email": "new@example.com",
        "password": "pass"
    })
    assert res2.status_code == 200
    assert "access_token" in res2.json()

async def test_login_wrong_password(client: AsyncClient, test_user):
    res = await client.post("/api/v1/auth/login", json={
        "email": "test@example.com",
        "password": "wrong"
    })
    assert res.status_code == 401

async def test_access_protected_route(client: AsyncClient):
    res = await client.get("/api/v1/projects/")
    assert res.status_code == 401
