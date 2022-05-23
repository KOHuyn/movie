package com.kohuyn.movie.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.kohuyn.movie.R
import com.kohuyn.movie.databinding.FragmentMainBinding
import com.kohuyn.movie.ui.favourite.FavouriteFragment
import com.kohuyn.movie.ui.home.HomeFragment
import com.kohuyn.movie.ui.main.adapter.MainPagerAdapter

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private val fragmentPagers by lazy {
        mutableListOf(
            PagerItem(
                fragment = HomeFragment(),
                idMenuPager = R.id.home,
                isSelected = true
            ),
            PagerItem(
                fragment = FavouriteFragment(),
                idMenuPager = R.id.favourite,
                isSelected = false
            ),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.viewPagerMain.apply {
            adapter = MainPagerAdapter(this@MainFragment, fragmentPagers.map { it.fragment })
            isUserInputEnabled = false
            currentItem = fragmentPagers.indexOfFirst { it.isSelected }
                .let { index -> if (index == -1) 0 else index }
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        binding.bottomBar.setOnItemSelectedListener { menu ->
            fragmentPagers.mapIndexed { index, pagerItem -> pagerItem.idMenuPager to index }
                .find { (idTabPager, _) -> idTabPager == menu.itemId }?.second?.let { index ->
                    binding.viewPagerMain.setCurrentItem(index, false)
                }
            true
        }
    }

    private data class PagerItem(
        val fragment: Fragment,
        @IdRes val idMenuPager: Int,
        var isSelected: Boolean,
    )
}