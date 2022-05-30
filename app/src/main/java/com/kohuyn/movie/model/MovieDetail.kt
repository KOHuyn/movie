package com.kohuyn.movie.model

import androidx.annotation.FloatRange

data class MovieDetail(
    val movieId: Int = -1,
    val backdropPath: String? = null,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val voteCount: Int?,
    val status: String?,
    val spokenLanguage: String?,
    val releaseDate: String?,
    val genres: String?,
    @FloatRange(from = 0.0, to = 10.0) val rating: Float = 0.0f,
    val movieRecommendations: List<MovieRecommendPreview> = emptyList(),
    val isFavorite: Boolean = false
)