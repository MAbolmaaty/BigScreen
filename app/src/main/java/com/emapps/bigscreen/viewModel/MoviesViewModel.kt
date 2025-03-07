package com.emapps.bigscreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.emapps.bigscreen.data.model.Movie
import com.emapps.bigscreen.data.repositories.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20
private const val MAX_SIZE = 60

@HiltViewModel
class MoviesViewModel @Inject constructor(private val repository: MoviesRepository) : ViewModel() {

    fun fetchBestMovies(year: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
                maxSize = MAX_SIZE),
            pagingSourceFactory = {
                repository.moviesPagingSource(
                    year
                )
            }
        ).flow.cachedIn(viewModelScope)
    }
}