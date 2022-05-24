package com.kohuyn.movie.model

data class Poster(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val adult: Boolean?,
    val overview: String?,
    val originalTitle: String?,
    val originalLanguage: String?,
    val backdropPath: String?,
    val popularity: Double,
    val voteCount: Int,
    val video: Boolean,
    val votePercent: Int
) {
    companion object {
        fun mockData(id: Int = -1): Poster {
            return Poster(
                id = id,
                title = "How to Move On in 30 Days",
                posterPath = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/2cLWJiDOZvhoIaX2r2Zb78K66JQ.jpg",
                releaseDate = "Apr 04, 2022",
                adult = false,
                overview = "We don't have an overview translated in English. Help us expand our database by adding one.",
                originalTitle = "How to Move On in 30 Days",
                originalLanguage = "English",
                backdropPath = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/2cLWJiDOZvhoIaX2r2Zb78K66JQ.jpg",
                popularity = 1.0,
                voteCount = 1,
                video = false,
                votePercent = 10
            )
        }
    }
}