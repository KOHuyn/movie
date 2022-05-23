package com.kohuyn.movie.model

data class Poster(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val adult: Boolean,
    val overview: String,
    val originalTitle: String,
    val originalLanguage: String,
    val backdropPath: String?,
    val popularity: Long,
    val voteCount: Int,
    val video: Boolean,
    val voteAverage: Int
)