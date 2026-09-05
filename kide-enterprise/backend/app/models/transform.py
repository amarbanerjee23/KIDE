from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database import Base

class TransformHistory(Base):
    __tablename__ = "transform_history"
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"), nullable=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    input_json = Column(Text)
    output_json = Column(Text)
    status = Column(String) # success/error/warning
    errors = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    project = relationship("Project", back_populates="transform_histories")
    user = relationship("User", back_populates="transform_histories")
