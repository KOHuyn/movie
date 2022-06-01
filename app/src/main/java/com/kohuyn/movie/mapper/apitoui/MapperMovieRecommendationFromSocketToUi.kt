package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.MovieRecommendPreview
import com.kohuyn.movie.model.response.PostersResponse
import com.kohuyn.movie.utils.MovieImageLoader

object MapperMovieRecommendationFromSocketToUi :
    Mapper<PostersResponse, List<MovieRecommendPreview>> {
    override fun mapperFrom(from: PostersResponse): List<MovieRecommendPreview> {
        return from.results.map { recommend ->
            MovieRecommendPreview(
                id = recommend.id ?: -1,
                title = recommend.title ?: "-",
                posterPath = MovieImageLoader.loadBackdropPath(recommend.backdropPath),
                ratePercent = recommend.voteAverage?.times(10)
                    ?.let { "%.2f".format(it) + "%" }
            )
        }
    }
}