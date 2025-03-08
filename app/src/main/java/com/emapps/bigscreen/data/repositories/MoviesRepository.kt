package com.emapps.bigscreen.data.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.emapps.bigscreen.data.database.dao.FavoriteMoviesDao
import com.emapps.bigscreen.data.model.Movie
import com.emapps.bigscreen.data.network.BigScreenApi
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

class MoviesRepository @Inject constructor(
    private val bigScreenApi: BigScreenApi,
    private val favoriteMoviesDao: FavoriteMoviesDao
) :
    BaseRepository() {

    fun moviesPagingSource(year: String) = MoviesPagingSource(
        bigScreenApi, year, favoriteMoviesDao)

    class MoviesPagingSource(
        private val bigScreenApi: BigScreenApi,
        private val year: String,
        private val favoriteMoviesDao: FavoriteMoviesDao
    ) : PagingSource<Int, Movie>() {

        private val startingKey = 1
        private val accessToken =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlNjViODZhODQ2YWE4NDU1YzU0NGI0OGYyMTU0NGZiYyIsIm5iZiI6MTc0MTI2Mjg4OS4wNDMsInN1YiI6IjY3Yzk5MDI5ZWY5YTkwY2RiYTI0YWEzYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.aR65Pog68xvB-gIhOt9Zi6j873x90JnE3i8wfNwkll8"
        private val fullDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        companion object {
            private val TAG = MoviesPagingSource::class.simpleName
        }

        override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
            val key = params.key ?: startingKey

            val response = bigScreenApi.fetchBestMovies(accessToken, key, year).body()
            val movies = response?.movies ?: listOf()
            val favoriteMovies = favoriteMoviesDao.fetchFavoriteMovies().map { it.title }
            Log.d(TAG, favoriteMovies.toString())
            movies.forEach { movie ->
                val date = fullDateFormat.parse(movie.releaseDate)
                date?.let {
                    movie.releaseDate = monthFormat.format(it)
                }
                movie.liked = movie.title in favoriteMovies
            }
            return LoadResult.Page(
                data = movies,
                prevKey = when (key) {
                    startingKey -> null
                    else -> when (val prevKey = ensureValidKey(key = key - 1)) {
                        0 -> null
                        else -> prevKey
                    }
                },
                nextKey = if (movies.isNotEmpty()) key + 1 else null
            )
        }

        private fun ensureValidKey(key: Int) = max(startingKey, key)
    }
}