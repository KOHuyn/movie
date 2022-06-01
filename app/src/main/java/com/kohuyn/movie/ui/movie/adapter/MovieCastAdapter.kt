package com.kohuyn.movie.ui.movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kohuyn.movie.databinding.ItemSeriesCastBinding
import com.kohuyn.movie.model.Cast

class MovieCastAdapter : RecyclerView.Adapter<MovieCastAdapter.MovieCastVH>() {
    class MovieCastVH(val binding: ItemSeriesCastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): MovieCastVH {
                return MovieCastVH(
                    ItemSeriesCastBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }
    }

    private var maxHeightItem: Int = 0

    var items: List<Cast> = emptyList()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCastVH {
        return MovieCastVH.create(parent)
    }

    override fun onBindViewHolder(holder: MovieCastVH, position: Int) {
        val item = items[position]
        with(holder) {
            Glide.with(binding.ivCast.context)
                .load(item.castPath)
                .centerCrop()
                .into(binding.ivCast)
            binding.tvNameCast.text = item.castName
            binding.tvNameCharacter.text = item.characterName

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