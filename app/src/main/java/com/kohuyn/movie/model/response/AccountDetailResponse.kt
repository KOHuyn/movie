package com.kohuyn.movie.model.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AccountDetailResponse(
    @SerializedName("avatar")
    val avatar: Avatar? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("iso_639_1")
    val iso6391: String? = null,
    @SerializedName("iso_3166_1")
    val iso31661: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("include_adult")
    val includeAdult: Boolean? = null,
    @SerializedName("username")
    val username: String? = null
) {
    @Keep
    data class Avatar(
        @SerializedName("gravatar")
        val gravatar: Gravatar? = null,
        @SerializedName("tmdb")
        val tmdb: Tmdb? = null
    ) {
        @Keep
        data class Gravatar(
            @SerializedName("hash")
            val hash: String? = null
        )

        @Keep
        data class Tmdb(
            @SerializedName("avatar_path")
            val avatarPath: Any? = null
        )
    }
}