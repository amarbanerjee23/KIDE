from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database import Base

class Organization(Base):
    __tablename__ = "organizations"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True)
    slug = Column(String, unique=True, index=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    users = relationship("User", back_populates="organization")
    projects = relationship("Project", back_populates="organization")

class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    email = Column(String, unique=True, index=True)
    hashed_password = Column(String)
    full_name = Column(String)
    org_id = Column(Integer, ForeignKey("organizations.id"))
    role = Column(String, default="member") # admin/member/viewer
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    organization = relationship("Organization", back_populates="users")
    projects = relationship("Project", back_populates="creator")
    transform_histories = relationship("TransformHistory", back_populates="user")
