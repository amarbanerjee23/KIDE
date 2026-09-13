from fastapi import APIRouter
from .auth import router as auth_router
from .projects import router as projects_router
from .admin import router as admin_router
from .parse import router as parse_router
from .transform import router as transform_router
from .export import router as export_router
from .validate import router as validate_router
from .generators import router as generators_router
from .knowledge import router as knowledge_router
from .knowledge_ingestion import router as knowledge_ingestion_router
from .simulation import router as simulation_router
from .ai import router as ai_router
from .traceability import router as traceability_router
from .organizations import router as organizations_router
from .billing import router as billing_router

api_router = APIRouter(prefix="/api/v1")

api_router.include_router(auth_router, prefix="/auth", tags=["auth"])
api_router.include_router(projects_router, prefix="/projects", tags=["projects"])
api_router.include_router(admin_router)
api_router.include_router(organizations_router)
api_router.include_router(billing_router)
api_router.include_router(knowledge_ingestion_router)
api_router.include_router(ai_router)
api_router.include_router(traceability_router)
api_router.include_router(parse_router)
api_router.include_router(transform_router)
api_router.include_router(export_router)
api_router.include_router(validate_router)
api_router.include_router(generators_router)
api_router.include_router(knowledge_router)
api_router.include_router(simulation_router)

