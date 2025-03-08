package com.emapps.bigscreen.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emapps.bigscreen.data.database.DatabaseConstants.FAVORITE_MOVIES_TABLE_NAME
import com.emapps.bigscreen.data.model.Movie

@Dao
interface FavoriteMoviesDao {

    @Query("SELECT * FROM $FAVORITE_MOVIES_TABLE_NAME")
    suspend fun fetchFavoriteMovies(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movie: Movie)

    @Query("DELETE FROM $FAVORITE_MOVIES_TABLE_NAME WHERE title = :title")
    suspend fun removeMovie(title: String)
}