package com.kohuyn.movie.model.response

import com.google.gson.annotations.SerializedName

data class CreateSessionResponse(
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("session_id")
    val sessionId: String?,
)