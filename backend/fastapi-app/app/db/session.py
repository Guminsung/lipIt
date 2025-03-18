from motor.motor_asyncio import AsyncIOMotorClient
import os
from dotenv import load_dotenv

load_dotenv()

MONGO_URI = os.getenv(
    # "MONGO_URI", "mongodb://arizona:ssafyd102@lipit-mongo:27017/lipit_db"
    "MONGO_URI",
    "mongodb://arizona:ssafyd102@localhost:27017/lipit_db?authSource=admin",  # authSource=admin을 반드시 추가해야 함 (MongoDB의 기본 인증 데이터베이스가 admin이기 때문)
)


class MongoDB:
    """MongoDB 연결 관리 클래스"""

    def __init__(self):
        self.client = None
        self.db = None

    async def connect(self):
        """MongoDB 연결"""
        self.client = AsyncIOMotorClient(MONGO_URI)
        self.db = self.client.get_database("lipit_db")

    async def close(self):
        """MongoDB 연결 종료"""
        if self.client:
            self.client.close()


mongodb = MongoDB()
