from typing import Any, Dict, List, Optional
from datetime import datetime
from pydantic import BaseModel, Field

class IngestionJobResponse(BaseModel):
    id: int
    job_uuid: str
    org_id: int
    user_id: Optional[int] = None
    filename: str
    file_type: str
    file_size_bytes: int
    status: str
    progress_pct: int
    stage_message: str
    extracted_devices_count: int
    extracted_capabilities_count: int
    extracted_operations_count: int
    extracted_parameters_count: int
    error_message: Optional[str] = None
    created_at: datetime
    completed_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class IngestionJobCreate(BaseModel):
    filename: str
    file_type: str = "txt"
    content: str

class SampleIngestRequest(BaseModel):
    sample_id: str

class StagedArtifactResponse(BaseModel):
    id: int
    artifact_uuid: str
    job_id: int
    org_id: int
    name: str
    category: str
    protocol: str
    status: str
    device_metadata: Dict[str, Any]
    extracted_datapoints: List[Dict[str, Any]]
    extracted_commands: List[Dict[str, Any]]
    extracted_alarms: List[Dict[str, Any]]
    extracted_capabilities: List[Dict[str, Any]]
    generated_dml: str
    generated_mnc: str
    generated_cap: str
    generated_op: str
    review_notes: Optional[str] = None
    reviewed_by_user_id: Optional[int] = None
    reviewed_at: Optional[datetime] = None
    created_at: datetime

    class Config:
        from_attributes = True

class ArtifactReviewActionRequest(BaseModel):
    notes: Optional[str] = None

class NotificationResponse(BaseModel):
    id: int
    org_id: Optional[int] = None
    user_id: Optional[int] = None
    recipient_email: str
    notification_type: str
    subject: str
    body_text: str
    body_html: Optional[str] = None
    status: str
    channel: str
    metadata_json: Optional[str] = None
    created_at: datetime

    class Config:
        from_attributes = True

class SampleTemplate(BaseModel):
    id: str
    name: str
    category: str
    protocol: str
    manufacturer: str
    description: str
    file_type: str
