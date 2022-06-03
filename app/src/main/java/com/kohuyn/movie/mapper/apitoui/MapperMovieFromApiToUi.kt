package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.model.response.PostersResponse
import com.kohuyn.movie.utils.MovieImageLoader

object MapperMovieFromApiToUi : Mapper<PostersResponse, List<Poster>> {
    override fun mapperFrom(from: PostersResponse): List<Poster> {
        return from.results.map { poster ->
            Poster(
                id = poster.id ?: -1,
                title = poster.title ?: "-",
                posterPath = MovieImageLoader.loadPoster(poster.posterPath),
                releaseDate = poster.releaseDate,
                adult = poster.adult,
                overview = poster.overview,
                originalTitle = poster.originalTitle,
                originalLanguage = poster.originalLanguage,
                backdropPath = poster.backdropPath,
                popularity = poster.popularity ?: 0.0,
                voteCount = poster.voteCount ?: 0,
                video = poster.video ?: false,
                votePercent = (poster.voteAverage ?: 0.0).times(10).toInt()
            )
        }
    }
}