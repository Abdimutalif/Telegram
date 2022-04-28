package com.example.telegramclone.ui.base

import androidx.fragment.app.Fragment
import com.example.telegramclone.MainActivity

open class BaseFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mAppDrawer.disableDrawer()
    }
}
