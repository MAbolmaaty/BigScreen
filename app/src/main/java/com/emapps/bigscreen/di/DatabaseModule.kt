package com.emapps.bigscreen.di

import android.content.Context
import com.emapps.bigscreen.data.database.MoviesDatabase
import com.emapps.bigscreen.data.database.dao.FavoriteMoviesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMoviesDatabase(@ApplicationContext context: Context): MoviesDatabase {
        return MoviesDatabase.buildDatabase(context)
    }

    @Provides
    @Singleton
    fun provideFavoriteMoviesDao(database: MoviesDatabase): FavoriteMoviesDao {
        return database.favoriteMoviesDao()
    }
}