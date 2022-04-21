package com.example.firebasechatapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.databinding.ReceivedBinding
import com.example.firebasechatapp.databinding.SentBinding
import com.example.firebasechatapp.model.MessageGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MessageGroupAdapter(private val messageGroupList: List<MessageGroup>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //, private val senderUid: String, private val groupName: String

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mDbRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    val ITEM_RECEIVED = 1
    val ITEM_SENT = 2

    class SendViewHolder(private val itemBinding: SentBinding):RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(sentMessage: MessageGroup) {
            itemBinding.tvSentMessage.text = sentMessage.message
        }
    }
    class ReceiveViewHolder(private val itemBinding: ReceivedBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(receivedMessage: MessageGroup) {
            itemBinding.tvReceivedMessage.text = receivedMessage.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            ReceiveViewHolder(
                ReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            SendViewHolder(
                SentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageGroupList[position]
        //Log.i("currentMessage", "$currentMessage")

        if (holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            viewHolder.bindItem(currentMessage)
        } else if (holder.javaClass == ReceiveViewHolder::class.java) {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.bindItem(currentMessage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageGroupList[position]
        return if (mAuth.currentUser!!.uid == currentMessage.senderId) {
//            Log.i("senderUid", senderUid)
//            Log.i("senderId", "${currentMessage.senderId}")
            ITEM_SENT
        } else {
            ITEM_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return messageGroupList.size
    }
}