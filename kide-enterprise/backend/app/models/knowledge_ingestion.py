import uuid
from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, DateTime, Text
from sqlalchemy.orm import relationship
from datetime import datetime, timezone
from app.database import Base

def utcnow():
    return datetime.now(timezone.utc)

def generate_uuid():
    return str(uuid.uuid4())

class IngestionJob(Base):
    __tablename__ = "ingestion_jobs"

    id = Column(Integer, primary_key=True, index=True)
    job_uuid = Column(String(36), default=generate_uuid, unique=True, index=True, nullable=False)
    org_id = Column(Integer, ForeignKey("organizations.id"), nullable=False, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=True, index=True)
    
    filename = Column(String(255), nullable=False)
    file_type = Column(String(50), nullable=False)  # pdf, txt, csv, json, yaml
    file_size_bytes = Column(Integer, default=0)
    raw_content = Column(Text, nullable=True)
    
    status = Column(String(50), default="pending", index=True)  # pending, processing, completed, failed
    progress_pct = Column(Integer, default=0)
    stage_message = Column(String(255), default="Job queued")
    
    extracted_devices_count = Column(Integer, default=0)
    extracted_capabilities_count = Column(Integer, default=0)
    extracted_operations_count = Column(Integer, default=0)
    extracted_parameters_count = Column(Integer, default=0)
    
    error_message = Column(Text, nullable=True)
    created_at = Column(DateTime(timezone=True), default=utcnow)
    completed_at = Column(DateTime(timezone=True), nullable=True)

    # Relationships
    artifacts = relationship("StagedKnowledgeArtifact", back_populates="job", cascade="all, delete-orphan")


class StagedKnowledgeArtifact(Base):
    __tablename__ = "staged_knowledge_artifacts"

    id = Column(Integer, primary_key=True, index=True)
    artifact_uuid = Column(String(36), default=generate_uuid, unique=True, index=True, nullable=False)
    job_id = Column(Integer, ForeignKey("ingestion_jobs.id"), nullable=False, index=True)
    org_id = Column(Integer, ForeignKey("organizations.id"), nullable=False, index=True)
    
    name = Column(String(255), nullable=False)
    category = Column(String(100), default="Sensors & Actuators")
    protocol = Column(String(50), default="Modbus-RTU")
    status = Column(String(50), default="draft", index=True)  # draft, approved, rejected, promoted
    
    device_metadata = Column(Text, default="{}")       # JSON dict: manufacturer, model, power, temp, ip_rating
    extracted_datapoints = Column(Text, default="[]")  # JSON list: parameters/registers
    extracted_commands = Column(Text, default="[]")    # JSON list: commands
    extracted_alarms = Column(Text, default="[]")      # JSON list: alarms
    extracted_capabilities = Column(Text, default="[]")# JSON list: capabilities
    
    generated_dml = Column(Text, default="")
    generated_mnc = Column(Text, default="")
    generated_cap = Column(Text, default="")
    generated_op = Column(Text, default="")
    
    review_notes = Column(Text, nullable=True)
    reviewed_by_user_id = Column(Integer, ForeignKey("users.id"), nullable=True)
    reviewed_at = Column(DateTime(timezone=True), nullable=True)
    created_at = Column(DateTime(timezone=True), default=utcnow)

    # Relationships
    job = relationship("IngestionJob", back_populates="artifacts")


class NotificationLog(Base):
    __tablename__ = "notification_logs"

    id = Column(Integer, primary_key=True, index=True)
    org_id = Column(Integer, ForeignKey("organizations.id"), nullable=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=True, index=True)
    
    recipient_email = Column(String(255), nullable=False, index=True)
    notification_type = Column(String(100), nullable=False, index=True)
    subject = Column(String(255), nullable=False)
    body_text = Column(Text, nullable=False)
    body_html = Column(Text, nullable=True)
    
    status = Column(String(50), default="sent")  # sent, queued, failed
    channel = Column(String(50), default="email") # email, in_app
    metadata_json = Column(Text, default="{}")
    created_at = Column(DateTime(timezone=True), default=utcnow)

