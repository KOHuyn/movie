package com.kohuyn.movie.network

import com.google.gson.JsonObject
import com.kohuyn.movie.model.response.AccountDetailResponse
import com.kohuyn.movie.model.response.CreateSessionResponse
import com.kohuyn.movie.model.response.PostersResponse
import com.kohuyn.movie.model.response.TokenResponse
import com.kohuyn.movie.utils.StorageCache
import retrofit2.http.*

interface MovieApiService {
    @GET("discover/movie")
    suspend fun getDiscoverMovie(): PostersResponse

    @GET("authentication/token/new")
    suspend fun getTokenNew(): TokenResponse

    @POST("authentication/token/validate_with_login")
    suspend fun validateWithLogin(@Body body: JsonObject): TokenResponse

    @POST("authentication/session/new")
    suspend fun createSession(@Body body: JsonObject): CreateSessionResponse

    @GET("account")
    suspend fun getAccountDetail(@Query("session_id") sessionId: String): AccountDetailResponse

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoritesMovie(
        @Path("account_id") accountId: Int = StorageCache.accountId ?: -1,
        @Query("session_id") sessionId: String? = StorageCache.sessionId
    ): PostersResponse

    @POST("account/{account_id}/favorite")
    suspend fun favorite(
        @Path("account_id") accountId: Int = StorageCache.accountId ?: -1,
        @Query("session_id") sessionId: String? = StorageCache.sessionId,
        @Body body: JsonObject
    )
}