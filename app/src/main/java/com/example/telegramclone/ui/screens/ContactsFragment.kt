package com.example.telegramclone.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.telegramclone.MainActivity
import com.example.telegramclone.R
import com.example.telegramclone.adapters.UserAdapter
import com.example.telegramclone.database.APP_ACTIVITY
import com.example.telegramclone.database.AUTH
import com.example.telegramclone.database.NODE_USERS
import com.example.telegramclone.database.REF_DATABASE_ROOT
import com.example.telegramclone.databinding.FragmentContactsBinding
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.ui.base.BaseFragment
import com.example.telegramclone.ui.screens.single_chat.SingleChatFragment
import com.example.telegramclone.utils.*
import com.google.firebase.database.DatabaseReference
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private val binding by viewBinding(FragmentContactsBinding::bind)

    lateinit var adapter: UserAdapter
    lateinit var contactList: ArrayList<CommonModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        APP_ACTIVITY.title = "Contacts"
        //initRecyclerView()
        contactList = ArrayList()
        REF_DATABASE_ROOT.child(NODE_USERS).addValueEventListener(AppValueEventListener {
            val children = it.children
            contactList.clear()
            children.forEach { iti ->
                val value = iti.getValue(CommonModel::class.java)
                if (value != null && value.id != AUTH.uid) {
                    contactList.add(value)
                }
                adapter.notifyDataSetChanged()
            }
        })

        adapter = UserAdapter(contactList, object : UserAdapter.OnItemClickListener {
            override fun onItem(account: CommonModel) {
                val bundle = Bundle()
                bundle.putSerializable("user", account)
                val fragment = SingleChatFragment()
                fragment.arguments = bundle
                replaceFragment(fragment)
            }
        })
        binding.contactsRecycleView.adapter = adapter
    }
}

