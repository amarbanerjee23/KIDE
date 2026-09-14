from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database import Base

class AuditLog(Base):
    __tablename__ = "audit_logs"

    id = Column(Integer, primary_key=True, index=True)
    org_id = Column(Integer, ForeignKey("organizations.id"), index=True, nullable=False)
    actor_id = Column(Integer, ForeignKey("users.id"), index=True, nullable=True)
    actor_email = Column(String, nullable=True)
    action = Column(String, index=True, nullable=False)
    target_type = Column(String, index=True, nullable=True)
    target_id = Column(String, nullable=True)
    details = Column(Text, nullable=True)
    ip_address = Column(String, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow, index=True)

    organization = relationship("Organization")
    actor = relationship("User")

