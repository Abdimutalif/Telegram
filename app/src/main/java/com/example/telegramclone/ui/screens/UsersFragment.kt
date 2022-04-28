package com.example.telegramclone.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.telegramclone.R
import com.example.telegramclone.databinding.FragmentUsersBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class UsersFragment : Fragment(R.layout.fragment_users) {

    private val binding by viewBinding(FragmentUsersBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}