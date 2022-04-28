package com.example.telegramclone.ui.screens.main_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegramclone.R
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.ui.groups.GroupChatFragment
import com.example.telegramclone.ui.screens.single_chat.SingleChatFragment
import com.example.telegramclone.utils.*
import de.hdodenhof.circleimageview.CircleImageView

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {
    private var listItems = mutableListOf<CommonModel>()
    private val TAG = "TTT"

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.main_list_item_name)
        val itemLastMessage: TextView = view.findViewById(R.id.main_list_last_message)
        val itemPhoto: CircleImageView = view.findViewById(R.id.main_list_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)

        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            showToast("Dfghjk")
            Log.d(TAG, "onCreateViewHolder: $listItems")
            when (listItems[holder.adapterPosition].type) {
                TYPE_CHAT -> {
                    val fragment = SingleChatFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("user", listItems[holder.adapterPosition])
                    fragment.arguments = bundle
                    replaceFragment(fragment)
                }
                TYPE_GROUP -> {
                    val fragment = GroupChatFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("group", listItems[holder.adapterPosition])
                    fragment.arguments = bundle
                    replaceFragment(fragment)
                }
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text = listItems[position].fullName
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}