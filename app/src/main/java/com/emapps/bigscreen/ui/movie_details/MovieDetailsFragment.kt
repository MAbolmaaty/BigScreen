package com.emapps.bigscreen.ui.movie_details

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.emapps.bigscreen.R
import com.emapps.bigscreen.data.database.dao.FavoriteMoviesDao
import com.emapps.bigscreen.data.model.Movie
import com.emapps.bigscreen.databinding.FragmentMovieDetailsBinding
import com.emapps.bigscreen.utils.CustomTypefaceSpan
import com.emapps.bigscreen.viewModel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access movie details"
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
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviesViewModel.selectedMovie?.let { movie ->
            setMovieDetails(movie)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MovieDetailsFragment()

        private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
    }

    private fun setMovieDetails(movie: Movie) {
        val title = String.format(Locale.getDefault(),
            "%s %s", movie.title, movie.originalLanguage)
        val spannableText = SpannableStringBuilder(title)
        try {
            val typeFace = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
            typeFace?.let {
                spannableText.setSpan(
                    CustomTypefaceSpan("", typeFace),
                    title.length - movie.originalLanguage.length, title.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableText.setSpan(
                    AbsoluteSizeSpan(requireContext().resources.getDimensionPixelSize(R.dimen.small_text)),
                    title.length - movie.originalLanguage.length, title.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        binding.title.text = spannableText
        Glide.with(requireContext())
            .load(POSTER_BASE_URL + movie.poster)
            .placeholder(R.drawable.bg_poster_2)
            .into(binding.poster)
        if (movie.liked) {
            likeMovie()
        } else {
            unlikeMovie()
        }
        binding.btnLike.setOnClickListener {
            if (movie.liked) {
                unlikeMovie()
                lifecycleScope.launch(IO) {
                    favoriteMoviesDao.removeMovie(movie.title)
                    withContext(Main){
                        moviesViewModel.updateMovie(movie)
                    }
                }
            } else {
                likeMovie()
                lifecycleScope.launch(IO) {
                    favoriteMoviesDao.addMovie(movie)
                    withContext(Main){
                        moviesViewModel.updateMovie(movie)
                    }
                }
            }
            movie.liked = !movie.liked
        }
        binding.overview.text = movie.overview
        binding.rate.text = String.format(Locale.getDefault(), "%.1f", movie.voteAverage)
        binding.votes.text = String.format(Locale.getDefault(), "%d", movie.votes)
    }

    private fun likeMovie() {
        binding.btnLike.text = getString(R.string.liked)
        binding.btnLike.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btnLike.setBackgroundResource(R.drawable.corner_12)
        binding.btnLike.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )
    }

    private fun unlikeMovie() {
        binding.btnLike.text = getString(R.string.add_to_favorites)
        binding.btnLike.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        binding.btnLike.setBackgroundResource(R.drawable.stroke_corner)
        binding.btnLike.backgroundTintList = null
    }
}