package com.kohuyn.movie.ui.movie.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kohuyn.movie.databinding.ItemMovieRecommendationBinding
import com.kohuyn.movie.model.MovieRecommendPreview

class MovieRecommendationAdapter :
    RecyclerView.Adapter<MovieRecommendationAdapter.MovieRecommendationVH>() {
    class MovieRecommendationVH(val binding: ItemMovieRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): MovieRecommendationVH {
                return MovieRecommendationVH(
                    ItemMovieRecommendationBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }
    }

    var items: List<MovieRecommendPreview> = emptyList()
        set(value) {
            if (field == value) return
            if (field.isNotEmpty()) {
                notifyItemRangeRemoved(0, field.size)
            }
            field = value
            if (value.isNotEmpty()) {
                notifyItemRangeInserted(0, value.size)
            }
        }

    var onItemClick: ((movieId: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieRecommendationVH {
        return MovieRecommendationVH.create(parent)
    }

    override fun onBindViewHolder(holder: MovieRecommendationVH, position: Int) {
        val item = items[position]
        with(holder) {
            Glide.with(binding.ivPoster.context)
                .load(item.posterPath)
                .into(binding.ivPoster)
            binding.tvNameDescribe.text = item.title

            binding.root.setOnClickListener { onItemClick?.invoke(item.id) }
        }
    }

    override fun getItemCount(): Int = items.size
}