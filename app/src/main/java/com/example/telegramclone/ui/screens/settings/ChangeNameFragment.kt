package com.example.telegramclone.ui.screens.settings

import android.os.Bundle
import android.view.View

import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentChangeNameBinding
import com.example.telegramclone.ui.base.BaseChangeFragment
import com.example.telegramclone.utils.*
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding


class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private val binding by viewBinding(FragmentChangeNameBinding::bind)

    override fun onResume() {
        super.onResume()
        initFullNameList()
    }

    private fun initFullNameList() {
        binding.apply {
            val fullNameList = USERModel.fullName.split(" ")
            if (USERModel.fullName.length > 1) {
                settingsInputName.setText(fullNameList[0])
                settingsInputSurname.setText(fullNameList[1])
            } else settingsInputName.setText(fullNameList[0])
        }
    }

    override fun change() {
        binding.apply {
            val name = settingsInputName.text.toString()
            val surname = settingsInputSurname.text.toString()
            showToast("onChange")

            if (name.isNotEmpty()) {
                val fullName = "$name $surname"
                setNameToDatabase(fullName)
            } else
                showToast(getString(R.string.settings_toast_name_is_empty))
        }
    }
}