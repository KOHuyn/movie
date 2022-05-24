package com.kohuyn.movie.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kohuyn.movie.databinding.FragmentHomeBinding
import com.kohuyn.movie.ui.home.adapter.PosterAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val vm by viewModels<HomeViewModel>()
    private val adapter by lazy { PosterAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRcv()
        observeViewModel()
        vm.loadPosters()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            vm.posters
                .flowWithLifecycle(lifecycle)
                .collect { posters ->
                    adapter.items = posters
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
                        Snackbar.make(binding.root, messageShow, Snackbar.LENGTH_SHORT).show()
                        vm.setMessageShown(messageShow)
                    }
                }
        }
    }

    private fun setupRcv() {
        binding.rcvPoster.adapter = adapter
        binding.rcvPoster.layoutManager = GridLayoutManager(context, 3)
        binding.rcvPoster.setHasFixedSize(true)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.rcvPoster.isGone = isLoading
        if (isLoading) {
            binding.loadingProgress.show()
        } else {
            binding.loadingProgress.hide()
        }
    }
}