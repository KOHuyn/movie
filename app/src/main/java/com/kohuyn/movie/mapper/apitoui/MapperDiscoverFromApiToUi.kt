package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.model.response.PostersResponse
import com.kohuyn.movie.utils.MovieImageLoader

object MapperDiscoverFromApiToUi : Mapper<PostersResponse.ItemPosterResponse, Poster> {
    override fun mapperFrom(from: PostersResponse.ItemPosterResponse): Poster {
        return Poster(
            id = from.id ?: -1,
            title = from.title ?: "-",
            posterPath = MovieImageLoader.load(from.posterPath),
            releaseDate = from.releaseDate,
            adult = from.adult,
            overview = from.overview,
            originalTitle = from.originalTitle,
            originalLanguage = from.originalLanguage,
            backdropPath = from.backdropPath,
            popularity = from.popularity ?: 0.0,
            voteCount = from.voteCount ?: 0,
            video = from.video ?: false,
            votePercent = (from.voteAverage ?: 0.0).times(10).toInt()
        )
    }
}