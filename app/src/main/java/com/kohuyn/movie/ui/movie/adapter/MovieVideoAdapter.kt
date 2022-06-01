package com.kohuyn.movie.ui.movie.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kohuyn.movie.databinding.ItemMovieVideoBinding
import com.kohuyn.movie.model.Video

class MovieVideoAdapter : RecyclerView.Adapter<MovieVideoAdapter.MovieVideoVH>() {
    class MovieVideoVH(val binding: ItemMovieVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): MovieVideoVH {
                return MovieVideoVH(
                    ItemMovieVideoBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }
    }

    var items: List<Video> = emptyList()
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

    var onItemClick: ((urlVideo: String?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVideoVH {
        return MovieVideoVH.create(parent)
    }

    override fun onBindViewHolder(holder: MovieVideoVH, position: Int) {
        val item = items[position]
        with(holder) {
            Glide.with(binding.ivThumbnail)
                .load(item.urlThumb)
                .centerCrop()
                .into(binding.ivThumbnail)
            binding.tvTitle.text = item.title
            binding.root.setOnClickListener {
                onItemClick?.invoke(item.urlVideo)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}