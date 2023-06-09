import com.ywauran.sapajari.data.remote.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Origin", "https://frontend-admin-sapajari-evcd2jlux-ywauran.vercel.app/") // Replace with the origin of your Android app
                        .build()
                    chain.proceed(request)
                }
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://backend-sapajari-sllsn77ftq-et.a.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        // Ini handle api yang dari flask
        fun getPredictApiService(): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Origin", "http://localhost:3001") // Replace with the origin of your Android app
                        .build()
                    chain.proceed(request)
                }
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://sapajari.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
