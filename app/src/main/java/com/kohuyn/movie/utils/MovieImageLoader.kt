package com.kohuyn.movie.utils

object MovieImageLoader {

    private const val BASE_POSTER_PATH = "https://image.tmdb.org/t/p/w342"
    private const val BASE_BACKDROP_PATH = "https://image.tmdb.org/t/p/w780"
    private const val YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v="
    private const val YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/"

    fun loadPoster(path: String?): String? {
        return path?.let { BASE_POSTER_PATH + path }
    }

    fun loadBackdropPath(path: String?): String? {
        return path?.let { BASE_BACKDROP_PATH + path }
    }

    fun loadFacePath(path: String?): String? {
        return path?.let { "https://www.themoviedb.org/t/p/w138_and_h175_face/$path" }
    }

    fun getYoutubeVideoPath(videoPath: String?): String {
        return YOUTUBE_VIDEO_URL + videoPath
    }

    fun getYoutubeThumbnailPath(thumbnailPath: String?): String {
        return "$YOUTUBE_THUMBNAIL_URL$thumbnailPath/default.jpg"
    }
}