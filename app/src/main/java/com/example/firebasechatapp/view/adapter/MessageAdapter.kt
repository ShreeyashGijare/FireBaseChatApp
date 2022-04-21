package com.example.firebasechatapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.databinding.ReceivedBinding
import com.example.firebasechatapp.databinding.SentBinding
import com.example.firebasechatapp.model.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val messageList: List<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVED = 1
    val ITEM_SENT = 2

    class SendViewHolder(private val itemBinding: SentBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(sentMessage: Message) {
            itemBinding.tvSentMessage.text = sentMessage.message
        }
    }
    class ReceiveViewHolder(private val itemBinding: ReceivedBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(receivedMessage: Message) {
            itemBinding.tvReceivedMessage.text = receivedMessage.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            ReceiveViewHolder(ReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            SendViewHolder(SentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if (holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            viewHolder.bindItem(currentMessage)
        } else if (holder.javaClass == ReceiveViewHolder::class.java) {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.bindItem(currentMessage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser!!.uid == currentMessage.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}