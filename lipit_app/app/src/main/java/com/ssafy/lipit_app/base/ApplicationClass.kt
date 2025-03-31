import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.lipit_app.BuildConfig
import com.ssafy.lipit_app.base.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    companion object {
        lateinit var retrofit: Retrofit
    }

    override fun onCreate() {
        super.onCreate()

        val serverUrl = BuildConfig.SERVER_URL
        Log.d("SERVER", "🌐 서버 주소: $serverUrl")

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            try {
                val decodedMessage =
                    message.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
                Log.e("POST", "log: message $decodedMessage")
            } catch (e: Exception) {
                Log.e("POST", "log: message (decode failed) $message")
            }
        }.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthInterceptor(this))
            .addInterceptor(loggingInterceptor)
            .build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
