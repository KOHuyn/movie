package com.kohuyn.movie.mapper.apitodb

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.entity.MovieEntity
import com.kohuyn.movie.model.response.PostersResponse

object MapperMovieFromDbToUi : Mapper<PostersResponse, List<MovieEntity>> {
    override fun mapperFrom(from: PostersResponse): List<MovieEntity> {
        return from.results.map { poster ->
            MovieEntity(
                adult = poster.adult,
                backdropPath = poster.backdropPath,
                genreIds = poster.genreIds,
                id = poster.id,
                originalLanguage = poster.originalLanguage,
                originalTitle = poster.originalTitle,
                overview = poster.overview,
                popularity = poster.popularity,
                posterPath = poster.posterPath,
                releaseDate = poster.releaseDate,
                title = poster.title,
                video = poster.video,
                voteAverage = poster.voteAverage,
                voteCount = poster.voteCount,
                page = from.page,
            )
        }
    }
}