from fastapi import APIRouter
from .auth import router as auth_router
from .projects import router as projects_router
from .admin import router as admin_router
from .parse import router as parse_router
from .transform import router as transform_router
from .export import router as export_router
from .validate import router as validate_router

api_router = APIRouter(prefix="/api/v1")

api_router.include_router(auth_router, prefix="/auth", tags=["auth"])
api_router.include_router(projects_router, prefix="/projects", tags=["projects"])
api_router.include_router(admin_router, prefix="/admin", tags=["admin"])
api_router.include_router(parse_router)
api_router.include_router(transform_router)
api_router.include_router(export_router)
api_router.include_router(validate_router)
