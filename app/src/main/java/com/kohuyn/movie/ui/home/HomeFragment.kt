package com.kohuyn.movie.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kohuyn.movie.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val vm by viewModels<HomeViewModel>()
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
        binding.btnStartCounter.setOnClickListener {
            vm.startOrStopCounter()
        }
        lifecycleScope.launch {
            vm.counter.flowWithLifecycle(lifecycle).collect {
                binding.tvCounter.text = it.toString()
            }
        }
        lifecycleScope.launch {
            vm.startCounter.flowWithLifecycle(lifecycle).collect { isStart ->
                binding.btnStartCounter.text = if (isStart) "Stop counter" else "Start counter"
            }
        }
        lifecycleScope.launchWhenResumed {
            vm.startCounter.flowWithLifecycle(lifecycle, minActiveState = Lifecycle.State.RESUMED).collectLatest { isStart ->
                while (isStart) {
                    delay(100)
                    vm.plusCounter()
                }
            }
        }
    }
}