from sqlalchemy import Column, BigInteger, DateTime, func
from sqlalchemy.dialects.postgresql import JSONB
from app.db.session import Base


class Call(Base):
    __tablename__ = "call"

    call_id = Column(BigInteger, primary_key=True, autoincrement=True)
    call_history_id = Column(BigInteger, nullable=False)  # 통화 기록 ID
    messages = Column(JSONB, nullable=True)  # 대화 메시지 (JSONB 형식)
    start_time = Column(
        DateTime(timezone=True), server_default=func.now(), nullable=False
    )
    end_time = Column(DateTime(timezone=True), nullable=True)
    updated_at = Column(
        DateTime(timezone=True),
        server_default=func.now(),
        onupdate=func.now(),
        nullable=False,
    )  # 수정 시간
    created_at = Column(
        DateTime(timezone=True), server_default=func.now(), nullable=False
    )  # 생성 시간
