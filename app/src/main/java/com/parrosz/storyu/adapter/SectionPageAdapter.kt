package com.parrosz.storyu.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.parrosz.storyu.StoryListFragment
import com.parrosz.storyu.StoryMapsFragment

class SectionPageAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = StoryListFragment()
            1 -> fragment = StoryMapsFragment()
        }
        return fragment as Fragment
    }
}