package com.emapps.bigscreen.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emapps.bigscreen.data.database.DatabaseConstants.FAVORITE_MOVIES_TABLE_NAME
import com.squareup.moshi.Json

@Entity(tableName = FAVORITE_MOVIES_TABLE_NAME)
data class Movie(
    @field:Json(name = "poster_path") val poster: String,
    @PrimaryKey val title: String,
    @field:Json(name = "vote_average") val voteAverage: Float,
    @field:Json(name = "vote_count") val votes: Int,
    @field:Json(name = "release_date") var releaseDate: String,
    @field:Json(name = "overview") val overview: String,
    @field:Json(name = "original_language") val originalLanguage: String,
    var liked: Boolean = false
)

