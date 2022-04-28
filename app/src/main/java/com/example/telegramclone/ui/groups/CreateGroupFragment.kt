package com.example.telegramclone.ui.groups

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.database.APP_ACTIVITY
import com.example.telegramclone.database.createGroupToDatabase
import com.example.telegramclone.databinding.FragmentCreateGroupBinding
import com.example.telegramclone.ui.base.BaseFragment
import com.example.telegramclone.ui.screens.main_list.HomeFragment
import com.example.telegramclone.utils.getPlurals
import com.example.telegramclone.utils.hideKeyBoard
import com.example.telegramclone.utils.replaceFragment
import com.example.telegramclone.utils.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding


class CreateGroupFragment() : BaseFragment(R.layout.fragment_create_group) {

    private val binding by viewBinding(FragmentCreateGroupBinding::bind)
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mUri = Uri.EMPTY
    val contactsList = AddContactsFragment.listContacts

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).title = "Add contact"

    }

    override fun onResume() {
        super.onResume()
        hideKeyBoard()
        initRecyclerView()
        binding.createGroupPhoto.setOnClickListener { addPhoto() }
        binding.createGroupBtnComplete.setOnClickListener {
            val groupName = binding.createGroupInputName.text.toString()
            if (groupName.isNotEmpty()) {
                createGroupToDatabase(groupName, mUri, contactsList) {
                    replaceFragment(HomeFragment())
                }
            } else {
                showToast("Write name!")
            }
        }
        binding.createGroupInputName.requestFocus()
        binding.createGroupCounts.text = getPlurals(contactsList.size)
    }


    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val mUri = CropImage.getActivityResult(data).uri
            binding.createGroupPhoto.setImageURI(mUri)
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.createGroupRecycleView
        mAdapter = AddContactsAdapter()

        mRecyclerView.adapter = mAdapter
        contactsList.forEach {
            mAdapter.updateListItems(it)
        }
    }
}