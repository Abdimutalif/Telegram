package com.example.telegramclone.ui.groups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentAddContactsBinding
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.ui.base.BaseFragment
import com.example.telegramclone.utils.AppValueEventListener
import com.example.telegramclone.utils.hideKeyBoard
import com.example.telegramclone.utils.replaceFragment
import com.example.telegramclone.utils.showToast
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

/* Главный фрагмент, содержит все чаты, группы и каналы с которыми взаимодействует пользователь*/

class AddContactsFragment : BaseFragment(R.layout.fragment_add_contacts) {

    private val binding by viewBinding(FragmentAddContactsBinding::bind)
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private val mRefContactList = REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessage = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItem = listOf<CommonModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        APP_ACTIVITY.title = "Add contact"
    }

    override fun onResume() {
        super.onResume()
        listContacts.clear()
        hideKeyBoard()
        initRecyclerView()
        binding.addContactsBtnNext.setOnClickListener {
            if (listContacts.isEmpty()) showToast("Add contact")
            else replaceFragment(CreateGroupFragment())
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.addContactsRecycleView
        mAdapter = AddContactsAdapter()
        mRefUsers.addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
            mListItem = snapshot.children.map { it.getCommonModel() }
            mListItem.forEach { model ->

                mRefUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { snapshot1 ->
                        val newModel = snapshot1.getCommonModel()

                        mRefMessage.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { snapshot2 ->
                                val tempList = snapshot2.children.map { it.getCommonModel() }

                                if (tempList.isEmpty()) {
                                    newModel.lastMessage = "Chat cleaned!"
                                } else {
                                    newModel.lastMessage = tempList[0].text
                                }

                                if (newModel.fullName.isEmpty()) {
                                    newModel.fullName = newModel.phone
                                }
                                mAdapter.updateListItems(newModel)
                            })
                    })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }
}
