package com.example.telegramclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegramclone.databinding.ContactItemBinding
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.utils.downloadAndSetImage

class UserAdapter(
    private val list: ArrayList<CommonModel>,
    val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<UserAdapter.Vh>() {

    inner class Vh(private var binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(account: CommonModel) {
            binding.apply {
                if (account.fullName.isEmpty())
                    contactFullname1.text = "Telegram"
                else
                    contactFullname1.text = account.fullName

                contactStatus.text = account.state
                contactPhoto.downloadAndSetImage(account.photoUrl)

                itemView.setOnClickListener {
                    onItemClickListener.onItem(account)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh =
        Vh(
            ContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: Vh, position: Int) =
        holder.onBind(list[position])

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onItem(account: CommonModel)
    }
}