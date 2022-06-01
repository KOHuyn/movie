package com.kohuyn.movie.model.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MovieVideoResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("results")
    val results: List<Video>? = null
) {
    @Keep
    data class Video(
        @SerializedName("iso_639_1")
        val iso6391: String? = null,
        @SerializedName("iso_3166_1")
        val iso31661: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("key")
        val key: String? = null,
        @SerializedName("site")
        val site: String? = null,
        @SerializedName("size")
        val size: Int? = null,
        @SerializedName("type")
        val type: String? = null,
        @SerializedName("official")
        val official: Boolean? = null,
        @SerializedName("published_at")
        val publishedAt: String? = null,
        @SerializedName("id")
        val id: String? = null
    )
}