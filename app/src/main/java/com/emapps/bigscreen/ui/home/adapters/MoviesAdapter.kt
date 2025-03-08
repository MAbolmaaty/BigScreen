package com.emapps.bigscreen.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emapps.bigscreen.R
import com.emapps.bigscreen.data.model.Movie
import com.emapps.bigscreen.databinding.ViewHolderMovieBinding
import java.util.Locale

class MoviesAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val onFavoriteClickListener: OnFavoriteClickListener,
) : PagingDataAdapter<Movie, MoviesAdapter.ViewHolder>(MOVIE_DIFF_CALLBACK) {

    fun interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    fun interface OnFavoriteClickListener {
        fun onItemClicked(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemBinding = ViewHolderMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(itemBinding, onItemClickListener, onFavoriteClickListener)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        getItem(position)?.let { holder.bindMovie(it) }
    }

    companion object {
        private val MOVIE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(
                oldItem: Movie,
                newItem: Movie
            ) = oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: Movie,
                newItem: Movie
            ) = oldItem == newItem

        }
    }

    class ViewHolder(
        private val itemBinding: ViewHolderMovieBinding,
        private val onItemClickListener: OnItemClickListener,
        private val onFavoriteClickListener: OnFavoriteClickListener
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val bounceAnimation = AnimationUtils.loadAnimation(
            itemBinding.root.context,
            R.anim.bounce
        )

        companion object {
            private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
        }

        init {
            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClicked(absoluteAdapterPosition)
            }
        }

        fun bindMovie(movie: Movie) {
            itemBinding.title.text = movie.title
            Glide.with(itemBinding.root.context)
                .load(POSTER_BASE_URL + movie.poster)
                .placeholder(R.drawable.bg_poster_2)
                .into(itemBinding.poster)
            itemBinding.rate.text = String.format(Locale.getDefault(), "%.1f", movie.voteAverage)
            itemBinding.votes.text = String.format(Locale.getDefault(), "%d", movie.votes)
            itemBinding.releaseDate.text = movie.releaseDate
            if (movie.liked) {
                itemBinding.icLike.setImageResource(R.drawable.ic_liked)
            } else {
                itemBinding.icLike.setImageResource(R.drawable.ic_like)
            }
            itemBinding.icLike.setOnClickListener {
                if (movie.liked) {
                    itemBinding.icLike.setImageResource(R.drawable.ic_like)
                } else {
                    itemBinding.icLike.setImageResource(R.drawable.ic_liked)
                    it.startAnimation(bounceAnimation)
                }
                movie.liked = !movie.liked
                onFavoriteClickListener.onItemClicked(absoluteAdapterPosition)
            }
            itemBinding.overview.text = movie.overview
        }
    }
}