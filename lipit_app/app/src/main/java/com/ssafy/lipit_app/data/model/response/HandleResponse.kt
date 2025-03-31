
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ssafy.lipit_app.data.model.response.BaseResponse
import com.ssafy.lipit_app.data.model.response.ErrorResponse
import retrofit2.Response

inline fun <reified T> handleResponse(response: Response<BaseResponse<T>>): Result<T> {
    return if (response.isSuccessful) ({
        val body = response.body()
        if (body != null) {
            if (body.statusCode == 200) {
                Result.success(body.data)
            } else {
                Result.failure(Exception("status ${body.statusCode}: ${body.message}"))
            }
        } else {
            Result.failure(Exception("response body is null"))
        }
    }) as Result<T> else {
        val errorBody = response.errorBody()?.string()

        if (errorBody.isNullOrEmpty()) {
            return Result.failure(Exception("request fail: unknown error (empty response)"))
        }

        Log.e("API_ERROR", "서버 응답 에러: $errorBody")

        return try {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            val status = errorResponse?.status ?: "Unknown Status"
            val errorMessage = errorResponse?.message ?: "Unknown error from server"
            Result.failure(Exception("request fail ($status): $errorMessage"))
        } catch (e: JsonSyntaxException) {
            Result.failure(Exception("request fail: ${errorBody ?: "Unknown error"}"))
        }
    }
}
