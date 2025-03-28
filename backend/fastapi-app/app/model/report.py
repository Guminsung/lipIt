from sqlalchemy import Column, BigInteger, Integer, String, DateTime, func
from app.db.session import Base


class Report(Base):
    __tablename__ = "report"
    
    report_id = Column(BigInteger, primary_key=True, autoincrement=True)
    member_id = Column(BigInteger, nullable=False)
    call_id = Column(BigInteger, nullable=False)
    call_duration = Column(Integer, nullable=False)
    celeb_video_url = Column(String, nullable=True)
    word_count = Column(Integer, nullable=True)
    sentence_count = Column(Integer, nullable=True)
    communication_summary = Column(String(500), nullable=True)
    feedback_summary = Column(String(500), nullable=True)
    updated_at = Column(
        DateTime(timezone=True),
        server_default=func.now(),
        onupdate=func.now(),
        nullable=False,
    )
    created_at = Column(
        DateTime(timezone=True), 
        server_default=func.now(), 
        nullable=False
    ) 