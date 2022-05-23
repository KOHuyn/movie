package com.kohuyn.movie.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(fm: Fragment, private val arrFragment: List<Fragment>) :
    FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return arrFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return arrFragment[position]
    }
}