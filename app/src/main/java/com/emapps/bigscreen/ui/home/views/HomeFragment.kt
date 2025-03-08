package com.emapps.bigscreen.ui.home.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emapps.bigscreen.R
import com.emapps.bigscreen.data.database.dao.FavoriteMoviesDao
import com.emapps.bigscreen.databinding.FragmentHomeBinding
import com.emapps.bigscreen.ui.home.adapters.MoviesAdapter
import com.emapps.bigscreen.ui.home.decorations.MovieItemDecoration
import com.emapps.bigscreen.ui.movie_details.MovieDetailsFragment
import com.emapps.bigscreen.viewModel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access home"
        }

    private val moviesViewModel: MoviesViewModel by activityViewModels()

    @Inject
    lateinit var favoriteMoviesDao: FavoriteMoviesDao

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
        Log.d(TAG, "HomeFragment onViewCreated")
        setBestMoviesList()
        fetchBestMovies(BEST_MOVIES_YEAR)
        observeMovieUpdate()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()

        private val TAG = HomeFragment::class.simpleName
        private const val BEST_MOVIES_YEAR = "2024"
    }

    private fun setBestMoviesList() {
        val adapter = MoviesAdapter(
            onItemClickListener = { position ->
                val selectedMovie = (binding.bestMoviesList.adapter as MoviesAdapter).peek(position)
                moviesViewModel.selectedMovie = selectedMovie
                //findNavController().navigate(R.id.action_home_to_details)
                navigateToMovieDetails()
            },
            onFavoriteClickListener = { position ->
                (binding.bestMoviesList.adapter as MoviesAdapter).peek(position)?.let { movie ->
                    if (movie.liked) {
                        lifecycleScope.launch(IO){
                            favoriteMoviesDao.addMovie(movie)
                        }
                    } else {
                        lifecycleScope.launch(IO){
                            favoriteMoviesDao.removeMovie(movie.title)
                        }
                    }
                }
            })
        binding.bestMoviesList.adapter = adapter
        binding.bestMoviesList.layoutManager = LinearLayoutManager(
            requireContext(), RecyclerView.VERTICAL, false
        )
        if (binding.bestMoviesList.itemDecorationCount == 0) {
            binding.bestMoviesList.addItemDecoration(MovieItemDecoration())
        }
    }

    private fun fetchBestMovies(year: String) {
        lifecycleScope.launch {
            val movies = moviesViewModel.fetchBestMovies(year)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                movies.collectLatest {
                    (binding.bestMoviesList.adapter as MoviesAdapter).submitData(it)
                }
            }
        }
    }

    private fun navigateToMovieDetails() {
        requireActivity().supportFragmentManager.commit {
            add(
                R.id.main_fragment_container,
                MovieDetailsFragment.newInstance()
            )
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    private fun observeMovieUpdate() {
        moviesViewModel.observeMovieUpdate(viewLifecycleOwner) { movie ->
            val adapter = (binding.bestMoviesList.adapter as MoviesAdapter)
            val movieIndex = adapter.snapshot().indexOf(movie)
            adapter.notifyItemChanged(movieIndex)
        }
    }
}