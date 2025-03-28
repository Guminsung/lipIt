from sqlalchemy import Column, BigInteger, String, DateTime, func, Integer, ForeignKey
from app.db.session import Base


class NativeExpression(Base):
    __tablename__ = "native_expression"
    
    native_expression_id = Column(BigInteger, primary_key=True, autoincrement=True)
    report_id = Column(BigInteger, ForeignKey("report.report_id"), nullable=False)
    my_sentence = Column(String, nullable=False)
    AI_sentence = Column(String, nullable=False)
    keyword = Column(String, nullable=False)
    keyword_korean = Column(String, nullable=False)
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