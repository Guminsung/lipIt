from fastapi import APIRouter

router = APIRouter()


@router.get("/calls")
def get_calls():
    return [{"call_id": "call_001", "user": "Alice"}]
