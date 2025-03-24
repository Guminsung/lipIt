from fastapi import APIRouter
from typing import Type, Dict, Any
from pydantic import BaseModel
from app.util.swagger_response import error_response, success_response


class BaseRouter(APIRouter):
    def api_doc(
        self,
        path: str,
        endpoint,
        response_model: Type[BaseModel],
        success_model: Type[BaseModel],
        success_example: Dict = None,
        errors: Dict[int, Dict[str, Any]] = {},
        **kwargs
    ):
        """
        공통 응답 구조 자동 적용: success + errors
        """
        responses = {**success_response(success_model, example=success_example)}

        for status_code, info in errors.items():
            responses.update(error_response(status_code, info["message"], info["code"]))

        super().add_api_route(
            path, endpoint, response_model=response_model, responses=responses, **kwargs
        )
