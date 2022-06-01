package com.kohuyn.movie.ui.favourite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kohuyn.movie.R
import com.kohuyn.movie.databinding.ItemMovieFavouriteBinding
import com.kohuyn.movie.model.Poster
import timber.log.Timber

class FavouriteAdapter : ListAdapter<Poster, FavouriteAdapter.FavouriteVH>(DIFF_UTILS) {

    companion object {
        val DIFF_UTILS = object : DiffUtil.ItemCallback<Poster>() {
            override fun areItemsTheSame(oldItem: Poster, newItem: Poster): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Poster, newItem: Poster): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: Poster, newItem: Poster): Any? {
                val isLoadingChange = oldItem.isLoading != newItem.isLoading
                return if (isLoadingChange) IsLoadingDiff
                else super.getChangePayload(oldItem, newItem)
            }
        }

        object IsLoadingDiff
    }

    var onRemoveItemListener: (id: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteVH {
        return FavouriteVH.create(parent)
    }

    override fun onBindViewHolder(holder: FavouriteVH, position: Int) {
        holder.bind(getItem(position)) { idPoster ->
            onRemoveItemListener(idPoster)
        }
    }

    override fun onBindViewHolder(
        holder: FavouriteVH,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            payloads.forEach {
                Timber.d(it::class.java.simpleName)
                if (it is IsLoadingDiff) {
                    holder.bindLoading(getItem(position).isLoading)
                }
            }
        }
    }

    class FavouriteVH(private val binding: ItemMovieFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Poster,
            onRemoveItemListener: (idPoster: Int) -> Unit
        ) {
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
            binding.progressRatePercent.progress = item.votePercent
            binding.tvRatePercent.text = "${item.votePercent}%"
            bindLoading(item.isLoading)
        }

        fun bindLoading(isLoading: Boolean) {
            binding.btnRemove.text =
                if (isLoading) null else binding.root.context.getString(R.string.remove)
            binding.progressLoading.isVisible = isLoading
            binding.btnRemove.isEnabled = isLoading.not()
        }

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