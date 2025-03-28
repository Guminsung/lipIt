# app/service/topic_extractor.py

from typing import Optional, List
from keybert import KeyBERT


# KeyBERT 모델은 글로벌로 한 번만 로드해서 재사용
kw_model = KeyBERT(model="all-MiniLM-L6-v2")  # 또는 원하는 모델로 변경


def extract_topic_from_news(content: str, top_n: int = 1) -> Optional[str]:
    """
    뉴스 본문에서 핵심 키워드 추출 → topic 용도로 사용
    """
    if not content:
        return None

    keywords: List[str] = []

    try:
        keywords = kw_model.extract_keywords(
            content, keyphrase_ngram_range=(1, 2), stop_words="english", top_n=top_n
        )
    except Exception as e:
        print(f"❌ 키워드 추출 중 오류 발생: {e}")
        return None

    # [('keyword', score), ...] → 첫 번째 키워드 문자열 반환
    return keywords[0][0] if keywords else None
