package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.Video
import com.kohuyn.movie.model.response.MovieVideoResponse
import com.kohuyn.movie.utils.DateTimeFormatter
import com.kohuyn.movie.utils.MovieImageLoader

object MapperMovieVideosFromApiToUi : Mapper<MovieVideoResponse, List<Video>> {
    override fun mapperFrom(from: MovieVideoResponse): List<Video> {
        return from.results?.filter { it.type == "Trailer" }
            ?.sortedByDescending {
                DateTimeFormatter.formatToTimestamp(
                    it.publishedAt,
                    "yyyy-MM-dd'T'HH:mm:ss.S'Z'"
                ) ?: 0L
            }
            ?.map {
                Video(
                    title = it.name,
                    urlVideo = MovieImageLoader.getYoutubeVideoPath(it.key),
                    urlThumb = MovieImageLoader.getYoutubeThumbnailPath(it.key)
                )
            } ?: emptyList()
    }
}