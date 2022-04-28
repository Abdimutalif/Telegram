package com.example.telegramclone.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegramclone.R
import com.example.telegramclone.database.CURRENT_UID
import com.example.telegramclone.ui.message_recycler_view.views.MessageView
import com.example.telegramclone.utils.asTime

class HolderTextMessage(view:View):RecyclerView.ViewHolder(view),MessageHolder {
   private val blocUserMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_message)
   private val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
   private val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)

   private val blocReceivedUserMessage: ConstraintLayout =
        view.findViewById(R.id.bloc_received_message)
   private val chatReceivedUserMessage: TextView = view.findViewById(R.id.chat_received_message)
   private val chatReceivedUserMessageTime: TextView =
        view.findViewById(R.id.chat_received_message_time)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocUserMessage.visibility = View.VISIBLE
            blocReceivedUserMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blocReceivedUserMessage.visibility = View.VISIBLE
            blocUserMessage.visibility = View.GONE
            chatReceivedUserMessage.text = view.text
            chatReceivedUserMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}