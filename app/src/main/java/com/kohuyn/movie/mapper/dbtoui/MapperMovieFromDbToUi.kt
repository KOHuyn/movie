package com.kohuyn.movie.mapper.dbtoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.model.entity.MovieEntity
import com.kohuyn.movie.utils.MovieImageLoader

object MapperMovieFromDbToUi : Mapper<List<MovieEntity>, List<Poster>> {
    override fun mapperFrom(from: List<MovieEntity>): List<Poster> {
        return from.map { movie ->
            Poster(
                id = movie.id ?: -1,
                title = movie.title ?: "-",
                posterPath = MovieImageLoader.loadPoster(movie.posterPath),
                releaseDate = movie.releaseDate,
                adult = movie.adult,
                overview = movie.overview,
                originalTitle = movie.originalTitle,
                originalLanguage = movie.originalLanguage,
                backdropPath = movie.backdropPath,
                popularity = movie.popularity ?: 0.0,
                voteCount = movie.voteCount ?: 0,
                video = movie.video ?: false,
                votePercent = (movie.voteAverage ?: 0.0).times(10).toInt()
            )
        }
    }
}