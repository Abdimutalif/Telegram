package com.example.telegramclone.ui.screens.main_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegramclone.MainActivity

import com.example.telegramclone.R
import com.example.telegramclone.adapters.ViewPagerAdapter
import com.example.telegramclone.database.*
import com.example.telegramclone.databinding.FragmentHomeBinding
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mREF_MAIN_LIST = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessage = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItem = listOf<CommonModel>()

    lateinit var adapter: ViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).title = "Telegram"

        adapter = ViewPagerAdapter(this)

        binding.apply {

            viewPager2.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                when (position) {
                    0 -> tab.text = "Chats"
                    1 -> tab.text = "Groups"
                }
            }.attach()
        }
    }

    override fun onResume() {
        super.onResume()
        hideKeyBoard()
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        //initRecyclerView()
        //(activity as MainActivity).mAppDrawer.enableDrawer()
    }

    /*private fun initRecyclerView() {
        mRecyclerView = binding.mainListRecycleView
        mAdapter = MainListAdapter()

        mREF_MAIN_LIST.addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
            mListItem = snapshot.children.map { it.getCommonModel() }
            mListItem.forEach { model ->

                when (model.type) {
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }

            }
        })
        mRecyclerView.adapter = mAdapter
    }

    private fun showGroup(model: CommonModel) {
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
    }

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
    }*/
}