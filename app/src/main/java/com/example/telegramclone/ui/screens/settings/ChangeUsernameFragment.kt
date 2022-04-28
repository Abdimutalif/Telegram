package com.example.telegramclone.ui.screens.settings

import android.os.Bundle
import android.view.*

import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentChangeUsernameBinding
import com.example.telegramclone.ui.base.BaseChangeFragment
import com.example.telegramclone.utils.*
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import java.util.*

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    private val binding by viewBinding(FragmentChangeUsernameBinding::bind)
    lateinit var newUsername: String

    override fun onResume() {
        super.onResume()
        binding.apply {
            settingsInputUsername.setText(USERModel.username)
        }
    }

    override fun change() {
        newUsername = binding.settingsInputUsername.text.toString().lowercase(Locale.getDefault())
        if (newUsername.isNotEmpty()) {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(newUsername)) {
                        showToast("such a user already exists")
                    } else {
                        changeUsername()
                    }
                })
        } else
            showToast("Field is empty")
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(newUsername)
                }
            }
    }
}