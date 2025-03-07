package com.emapps.bigscreen.data.model

import com.squareup.moshi.Json

class MoviesModelResponse (
    val page: Int,
    @field:Json(name = "results") val movies: List<Movie>,
)