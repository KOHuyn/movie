package com.kohuyn.movie.ui.movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kohuyn.movie.databinding.ItemMovieRecommendationBinding
import com.kohuyn.movie.model.MovieRecommendPreview
import timber.log.Timber

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

    private var maxHeightItem: Int = 0
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
            binding.tvTitle.text = item.title
            binding.tvRatePercent.text = item.ratePercent
            binding.root.let { v ->
                if (v.measuredHeight in 1 until maxHeightItem) {
                    v.updateLayoutParams {
                        height = maxHeightItem
                    }
                }
            }
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.root.setHeightLayout(adapterPosition)
                    if (binding.root.measuredHeight == maxHeightItem) {
                        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            })
            binding.root.setOnClickListener { onItemClick?.invoke(item.id) }
        }
    }

    private fun View.setHeightLayout(position: Int) {
        if (position !in 0 until itemCount) return
        when {
            maxHeightItem < measuredHeight -> {
                maxHeightItem = measuredHeight
                post {
                    notifyDataSetChanged()
                }
            }
            measuredHeight in 1 until maxHeightItem -> {
                updateLayoutParams {
                    height = maxHeightItem
                }
                post {
                    notifyItemChanged(position)
                }
            }
            else -> {}
        }
    }

    override fun getItemCount(): Int = items.size
}