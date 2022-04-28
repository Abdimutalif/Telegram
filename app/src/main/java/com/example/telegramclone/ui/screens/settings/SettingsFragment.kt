package com.example.telegramclone.ui.screens.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment

import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentSettingsBinding
import com.example.telegramclone.ui.base.BaseFragment
import com.example.telegramclone.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val TAG = "SettingsFragment"

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.setting_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }
            R.id.setting_menu_change_name -> {
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }

    private fun initFields() {
        binding.apply {
            settingsBio.text = USERModel.bio
            settingsFullName.text = USERModel.fullName
            settingsPhoneNumber.text = USERModel.phone
            settingsStatus.text = USERModel.state
            settingsUsername.text = USERModel.username
            settingsBtnChangeUsername.setOnClickListener {
                replaceFragment(ChangeUsernameFragment())
            }
            settingsBtnChangeBio.setOnClickListener {
                replaceFragment(ChangeBioFragment())
            }
            settingsChangePhoto.setOnClickListener {
                changePhotoUser()
            }
            if (USERModel.photoUrl.isNotEmpty()) {
                settingsUserPhoto.downloadAndSetImage(USERModel.photoUrl)
            } else {
                settingsUserPhoto.setImageResource(R.drawable.default_photo)
            }
        }
    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)

            putFileToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        binding.settingsUserPhoto.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USERModel.photoUrl = it
                        (activity as MainActivity).mAppDrawer.upDateHeader()
                    }
                }
            }
        }
    }

}