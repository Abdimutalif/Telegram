package com.example.telegramclone.ui.screens.settings

import android.os.Bundle
import android.view.View
import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentChangeBioBinding
import com.example.telegramclone.ui.base.BaseChangeFragment
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    private val binding by viewBinding(FragmentChangeBioBinding::bind)

    override fun onResume() {
        super.onResume()
        binding.apply {
            settingsInputBio.setText(USERModel.bio)
        }
    }

    override fun change() {
        super.change()
        val newBio = binding.settingsInputBio.text.toString()
        setBioToDatabase(newBio)
    }
}