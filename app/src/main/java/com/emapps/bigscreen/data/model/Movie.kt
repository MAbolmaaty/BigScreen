package com.emapps.bigscreen.data.model

import com.squareup.moshi.Json

data class Movie(
    @field:Json(name = "poster_path") val poster: String,
    val title: String,
)
