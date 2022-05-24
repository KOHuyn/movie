package com.kohuyn.movie.network

import com.kohuyn.movie.config.Configs
import com.kohuyn.movie.model.response.StatusResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitUtils {
    private val retrofit = createRetrofit()
    val apiService: MovieApiService = retrofit.create(MovieApiService::class.java)

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Configs.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.HEADERS) })
            .addInterceptor { chain ->
                //add query api key wherever request http
                val request = chain.request()
                val httpUrl =
                    request.url.newBuilder().addQueryParameter("api_key", Configs.KEY_TMDB).build()
                request.newBuilder().url(httpUrl).build().let { chain.proceed(it) }
            }
            .connectTimeout(Configs.HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }

    fun <T> checkSuccess(
        response: Response<T>,
        onError: (StatusResponse) -> Unit
    ): Boolean {
        if (response.isSuccessful) {
            return true
        }
        val responseBody: ResponseBody = response.errorBody() ?: kotlin.run {
            onError(StatusResponse(-1, "Unknown error"))
            return false
        }
        val status = retrofit.responseBodyConverter<StatusResponse>(
            StatusResponse::class.java,
            StatusResponse::class.java.annotations
        ).convert(responseBody) as StatusResponse
        onError(status)
        return false
    }
}