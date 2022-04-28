package com.example.telegramclone.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.telegramclone.ui.screens.ChatsFragment
import com.example.telegramclone.ui.screens.GroupsFragment
import com.example.telegramclone.ui.screens.UsersFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> GroupsFragment()
            else -> ChatsFragment()
        }
    }
}