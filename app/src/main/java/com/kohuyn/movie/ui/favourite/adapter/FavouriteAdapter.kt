package com.kohuyn.movie.ui.favourite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kohuyn.movie.databinding.ItemMovieFavouriteBinding
import com.kohuyn.movie.model.Poster

class FavouriteAdapter : ListAdapter<Poster, FavouriteAdapter.FavouriteVH>(DIFF_UTILS) {

    companion object {
        val DIFF_UTILS = object : DiffUtil.ItemCallback<Poster>() {
            override fun areItemsTheSame(oldItem: Poster, newItem: Poster): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Poster, newItem: Poster): Boolean {
                return oldItem == newItem
            }
        }
    }

    var onRemoveItemListener: (id: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteVH {
        return FavouriteVH.create(parent)
    }

    override fun onBindViewHolder(holder: FavouriteVH, position: Int) {
        with(holder) {
            val item = getItem(position)
            Glide.with(binding.root.context)
                .load(item.posterPath)
                .centerCrop()
                .into(binding.ivPoster)

            binding.tvTitlePoster.text = item.title
            binding.tvDatePoster.text = item.releaseDate
            binding.btnRemove.setOnClickListener {
                onRemoveItemListener(item.id)
            }
            binding.tvDescribe.text = item.overview
            //todo fake
            binding.progressRatePercent.progress = 10
            binding.tvRatePercent.text = "10%"
        }
    }

    class FavouriteVH(val binding: ItemMovieFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): FavouriteVH {
                return ItemMovieFavouriteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).let { binding ->
                    FavouriteVH(binding)
                }
            }
        }
    }
}