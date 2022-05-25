package com.kohuyn.movie.network

import com.kohuyn.movie.model.response.DiscoverResponse
import retrofit2.http.GET

interface MovieApiService {
    @GET("discover/movie")
    suspend fun getDiscover(): DiscoverResponse
}