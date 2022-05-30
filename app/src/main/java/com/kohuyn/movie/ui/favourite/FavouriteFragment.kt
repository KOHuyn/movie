package com.kohuyn.movie.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kohuyn.movie.R
import com.kohuyn.movie.databinding.FragmentFavouriteBinding
import com.kohuyn.movie.ui.alert.MessageDialog
import com.kohuyn.movie.ui.favourite.adapter.FavouriteAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private val vm by viewModels<FavouriteViewModel>()
    private val adapter by lazy { FavouriteAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRcv()
        observeViewModel()
        initListener()
        vm.loadPosters()
    }

    private fun initListener() {
        adapter.onRemoveItemListener = { idPoster ->
            vm.removeFavorite(idPoster)
        }

        binding.refreshLayout.setOnRefreshListener {
            vm.loadPosters()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            vm.posters
                .flowWithLifecycle(lifecycle)
                .collect { posters ->
                    adapter.submitList(posters)
                }
        }
        lifecycleScope.launch {
            vm.loading
                .flowWithLifecycle(lifecycle)
                .collect { isLoading ->
                    setLoading(isLoading)
                }
        }

        lifecycleScope.launch {
            vm.messages
                .flowWithLifecycle(lifecycle)
                .collect { messages ->
                    if (messages.isNotEmpty()) {
                        val messageShow = messages.first()
                        MessageDialog.Builder()
                            .setMessage(messageShow.message)
                            .setButtonPositive { dialog ->
                                dialog.dismiss()
                            }
                            .setOnDismissListener { vm.setMessageShown(messageShow.message) }
                            .setCancelable(false)
                            .build(childFragmentManager)
                    }
                }
        }
    }

    private fun setupRcv() {
        binding.rcvFavourite.adapter = adapter
        binding.rcvFavourite.layoutManager = LinearLayoutManager(context)
        binding.rcvFavourite.setHasFixedSize(true)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.rcvFavourite.isGone = isLoading
        if (isLoading) {
            binding.loadingProgress.show()
        } else {
            binding.loadingProgress.hide()
        }
    }
}