package com.example.firebasechatapp.view.adapter

import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechatapp.databinding.DialogAddUserItemBinding
import com.example.firebasechatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddUserToGroupAdapter(private val activity: Activity, private val listItems: ArrayList<User>,):
    RecyclerView.Adapter<AddUserToGroupAdapter.ViewHolder>() {

    //Global Variables
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mDbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var groupName: String = activity.intent.getStringExtra("name").toString()

    class ViewHolder(view: DialogAddUserItemBinding): RecyclerView.ViewHolder(view.root) {
        val tvText = view.tvText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: DialogAddUserItemBinding = DialogAddUserItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userName = listItems[position]
        val name = userName.name.toString()
        val uid = userName.uid.toString()
        holder.tvText.text = name

        Log.e("GroupName", groupName)

        holder.itemView.setOnClickListener {
            mDbRef.child("Groups").child(groupName).child("Members").child(uid).setValue(User(uid, name))

        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}