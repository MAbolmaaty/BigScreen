package com.emapps.bigscreen.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emapps.bigscreen.data.model.Movie
import com.emapps.bigscreen.databinding.ViewHolderMovieBinding
import com.emapps.bigscreen.R

class MoviesAdapter() : PagingDataAdapter<Movie, MoviesAdapter.ViewHolder>(MOVIE_DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemBinding = ViewHolderMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(itemBinding)
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
        private val itemBinding: ViewHolderMovieBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        companion object {
            private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
        }

        fun bindMovie(movie: Movie) {
            itemBinding.title.text = movie.title
            Glide.with(itemBinding.root.context)
                .load(POSTER_BASE_URL + movie.poster)
                .placeholder(R.drawable.bg_poster_2)
                .into(itemBinding.poster)
        }
    }
}