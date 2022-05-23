package com.kohuyn.movie.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kohuyn.movie.databinding.ItemMoviePosterBinding
import com.kohuyn.movie.model.Poster

class PosterAdapter : RecyclerView.Adapter<PosterAdapter.PosterVH>() {

    var items: List<Poster> = listOf()
        set(value) {
            if (field.isNotEmpty()) {
                notifyItemRangeRemoved(0, field.size)
            }
            field = value
            if (value.isNotEmpty()) {
                notifyItemRangeInserted(0, value.size)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterVH {
        return PosterVH.create(parent)
    }

    override fun onBindViewHolder(holder: PosterVH, position: Int) {
        with(holder) {
            val item = items[position]
            Glide.with(binding.root.context)
                .load(item.posterPath)
                .centerCrop()
                .into(binding.ivPoster)

            binding.tvTitlePoster.text = item.title
            binding.tvDatePoster.text = item.releaseDate
            //todo fake
            binding.progressRatePercent.progress = 10
            binding.tvRatePercent.text = "10%"
        }
    }

    override fun getItemCount(): Int = items.size

    class PosterVH(val binding: ItemMoviePosterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): PosterVH {
                return ItemMoviePosterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).let { binding ->
                    PosterVH(binding)
                }
            }
        }
    }
}