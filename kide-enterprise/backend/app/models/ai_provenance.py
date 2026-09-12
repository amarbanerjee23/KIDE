from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime, timezone
from app.database import Base

def utcnow():
    return datetime.now(timezone.utc)

class AIProvenance(Base):
    __tablename__ = "ai_provenance"

    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), index=True)
    user_id = Column(Integer, ForeignKey("users.id"), index=True)
    session_id = Column(String, index=True)
    provider = Column(String, default="gemini")
    model_name = Column(String, default="gemini-1.5-flash")
    user_prompt = Column(Text)
    tool_calls = Column(Text, nullable=True)  # JSON array of tools called
    tool_results = Column(Text, nullable=True) # JSON array of results returned
    assistant_response = Column(Text)
    proposed_patches = Column(Text, nullable=True) # JSON array of proposed patches
    status = Column(String, default="proposed") # proposed, approved, rejected, applied
    created_at = Column(DateTime, default=utcnow)
    applied_at = Column(DateTime, nullable=True)

    project = relationship("Project")
    user = relationship("User")

