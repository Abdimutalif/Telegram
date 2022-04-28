package com.example.telegramclone.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegramclone.R
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentChatsBinding
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.ui.screens.main_list.MainListAdapter
import com.example.telegramclone.utils.AppValueEventListener
import com.example.telegramclone.utils.TYPE_CHAT
import com.example.telegramclone.utils.hideKeyBoard
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private val binding by viewBinding(FragmentChatsBinding::bind)

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mREF_MAIN_LIST = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessage = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItem = listOf<CommonModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        hideKeyBoard()

        //(activity as MainActivity).mAppDrawer.enableDrawer()
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.mainListRecycleView
        mAdapter = MainListAdapter()

        mREF_MAIN_LIST.addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
            mListItem = snapshot.children.map { it.getCommonModel() }
            mListItem.forEach { model ->
                if (model.type == TYPE_CHAT) {
                    showChat(model)
                }
                /*when (model.type) {
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }*/

            }
        })
        mRecyclerView.adapter = mAdapter
    }

 /*   private fun showGroup(model: CommonModel) {
        REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener { snapshot1 ->
                val newModel = snapshot1.getCommonModel()

                REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES)
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { snapshot2 ->
                        val tempList = snapshot2.children.map { it.getCommonModel() }

                        if (tempList.isEmpty()) {
                            newModel.lastMessage = "Chat cleaned!"
                        } else {
                            newModel.lastMessage = tempList[0].text
                        }
                        newModel.type = TYPE_GROUP
                        mAdapter.updateListItems(newModel)
                    })
            })
    }*/

    private fun showChat(model: CommonModel) {
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
                        newModel.type = TYPE_CHAT
                        mAdapter.updateListItems(newModel)
                    })
            })
    }
}