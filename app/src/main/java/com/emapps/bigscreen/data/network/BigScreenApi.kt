package com.emapps.bigscreen.data.network

import com.emapps.bigscreen.data.model.MoviesModelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface BigScreenApi {

    @Headers("Accept: application/json")
    @GET("discover/movie?language=en-US&sort_by=vote_average.desc&vote_count.gte=100")
    suspend fun fetchBestMovies(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("primary_release_year") year: String
    ): Response<MoviesModelResponse>
}