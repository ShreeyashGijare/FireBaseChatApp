package com.example.firebasechatapp.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.databinding.RvUserBinding
import com.example.firebasechatapp.model.Group
import com.example.firebasechatapp.view.activity.ChatActivity

class GroupAdapter(val context: Context, private val groupList: List<Group>): RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {
    class GroupViewHolder(private val itemBinding: RvUserBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(group: Group) {
            itemBinding.tvUserName.text = group.groupName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(RvUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = groupList[position]
        holder.bindItem(currentGroup)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentGroup.groupName)
            intent.putExtra("isGroup", 1)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}