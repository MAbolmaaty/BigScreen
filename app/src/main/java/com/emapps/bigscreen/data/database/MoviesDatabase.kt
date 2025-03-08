package com.emapps.bigscreen.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emapps.bigscreen.data.database.dao.FavoriteMoviesDao
import com.emapps.bigscreen.data.model.Movie

const val DATABASE_VERSION = 1

@Database(
    entities = [Movie::class],
    version = DATABASE_VERSION
)
abstract class MoviesDatabase : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "MoviesDB"

        fun buildDatabase(context: Context): MoviesDatabase {
            return Room.databaseBuilder(
                context,
                MoviesDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun favoriteMoviesDao(): FavoriteMoviesDao
}