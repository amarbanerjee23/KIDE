from fastapi import APIRouter
from app.routers import auth, projects, transform, export, admin, validate

api_router = APIRouter()
api_router.include_router(auth.router, prefix="/api/v1/auth", tags=["auth"])
api_router.include_router(projects.router, prefix="/api/v1/projects", tags=["projects"])
api_router.include_router(transform.router, prefix="/api/v1/transform", tags=["transform"])
api_router.include_router(export.router, prefix="/api/v1/export", tags=["export"])
api_router.include_router(admin.router, prefix="/api/v1/admin", tags=["admin"])
api_router.include_router(validate.router, prefix="/api/v1/validate", tags=["validate"])
