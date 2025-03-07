package com.emapps.bigscreen.ui.home.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emapps.bigscreen.databinding.FragmentHomeBinding
import com.emapps.bigscreen.ui.home.adapters.MoviesAdapter
import com.emapps.bigscreen.ui.home.decorations.MovieItemDecoration
import com.emapps.bigscreen.viewModel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val moviesViewModel: MoviesViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access home"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBestMoviesList()
        fetchBestMovies(BEST_MOVIES_YEAR)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()

        private const val BEST_MOVIES_YEAR = "2024"
    }

    private fun setBestMoviesList() {
        val adapter = MoviesAdapter()
        binding.bestMoviesList.adapter = adapter
        binding.bestMoviesList.layoutManager = LinearLayoutManager(
            requireContext(), RecyclerView.HORIZONTAL, false
        )
        if (binding.bestMoviesList.itemDecorationCount == 0) {
            binding.bestMoviesList.addItemDecoration(MovieItemDecoration())
        }
    }

    private fun fetchBestMovies(year: String) {
        val movies = moviesViewModel.fetchBestMovies(year)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                movies.collectLatest {
                    (binding.bestMoviesList.adapter as MoviesAdapter).submitData(it)
                }
            }
        }
    }
}