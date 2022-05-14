package com.example.firebasechatapp.view.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.R
import com.example.firebasechatapp.databinding.ActivityChatBinding
import com.example.firebasechatapp.databinding.DialogAddUserListBinding
import com.example.firebasechatapp.model.Group
import com.example.firebasechatapp.model.Message
import com.example.firebasechatapp.model.MessageGroup
import com.example.firebasechatapp.model.User
import com.example.firebasechatapp.view.adapter.AddUserToGroupAdapter
import com.example.firebasechatapp.view.adapter.MessageAdapter
import com.example.firebasechatapp.view.adapter.MessageGroupAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.protobuf.Value
import java.lang.NullPointerException


class ChatActivity : BaseActivity() {

//    private lateinit var chatRecyclerView: RecyclerView
//    private lateinit var messageAdapterTwo: MessageAdapterTwo
//    private lateinit var messageList: ArrayList<Message>

    private var binding: ActivityChatBinding? = null
    private var userList: ArrayList<User> = ArrayList()
    private var messageList: ArrayList<Message> = ArrayList()
    private var messageGroupList: ArrayList<MessageGroup> = ArrayList()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var mAddUserToGroupDialog: Dialog
    private var isGroup: Int? = null

    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        isGroup = intent?.getIntExtra("isGroup", 0)
        groupName = intent?.getStringExtra("name")

        setUpToolbarContent()
        sendMessage()
        if (isGroup == 0) {
            setSingleChatUpAdapter()
        } else if (isGroup == 1) {
            setGroupChatUpAdapter()
        }

    }

    private fun setSingleChatUpAdapter() {
        val rvChatAdapter = binding?.rvChat
        rvChatAdapter?.layoutManager = LinearLayoutManager(this)
        val adapter = MessageAdapter(messageList)
        rvChatAdapter?.adapter = adapter

        //Logic for adding data to recyclerview
            mDbRef.child("Chats").child(senderRoom!!).child("messages")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageList.clear()
                        for (postSnapshot in snapshot.children) {
                            val message = postSnapshot.getValue(Message::class.java)
                            messageList.add(message!!)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

    }

    private fun setGroupChatUpAdapter() {
        val rvChatAdapter = binding?.rvChat
        rvChatAdapter?.layoutManager = LinearLayoutManager(this)
        val adapter = MessageGroupAdapter(messageGroupList)
        //,mAuth.currentUser!!.uid,groupName!!
        rvChatAdapter?.adapter = adapter

        try {
            mDbRef.child("Groups").child(groupName!!).child("GroupMessages")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messageGroupList.clear()
                        for (postSnapshot in snapshot.children) {
                            val messageGroup = postSnapshot.getValue(MessageGroup::class.java)
                            messageGroupList.add(messageGroup!!)
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun sendMessage() {
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        binding?.btnSend?.setOnClickListener {
            val message = binding?.etMsgBox?.text.toString()
            if (isGroup == 0) {
                val messageObject = Message(message, senderUid)
                mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                binding?.etMsgBox?.setText("")

            } else if (isGroup == 1) {
                val messageGroupObject = MessageGroup(message, senderUid)
//                senderRoomGroup = groupName + senderUid
//                receiverRoomGroup = senderUid + groupName
                mDbRef.child("Groups").child(groupName!!).child("GroupMessages").push()
                    .setValue(messageGroupObject)
                    .addOnSuccessListener {
//                        mDbRef.child("Groups").child(groupName!!).child("GroupMessages")
//                            .push().setValue(messageGroupObject)
                    }
                binding?.etMsgBox?.setText("")
                Log.i("groupName","$groupName")
            }

        }
    }

    /*private fun sendGroupMessage() {
        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
    }*/


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //groupName = intent.getStringExtra("name").toString()
        //isGroup = intent?.getIntExtra("isGroup", 0)
        if (isGroup == 0) {
            menuInflater.inflate(R.menu.menu_chat, menu)
        } else if(isGroup == 1) {
            menuInflater.inflate(R.menu.menu_group, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addUser) {

            addUserToGroup()

            Toast.makeText(this, "Add User clicked", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.showGroupDetails) {
            Toast.makeText(this, "Group Details clicked", Toast.LENGTH_SHORT).show()
            return true
        }else if (item.itemId == R.id.showProfile) {
            Toast.makeText(this, "Show profile clicked", Toast.LENGTH_SHORT).show()
            return true
        } else {
            Toast.makeText(this, "Block clicked", Toast.LENGTH_SHORT).show()
            return true
        }
        return true
    }

    private fun addUserToGroup(){
        mAddUserToGroupDialog = Dialog(this)
        val dBinding: DialogAddUserListBinding = DialogAddUserListBinding.inflate(layoutInflater)

        mAddUserToGroupDialog.setContentView(dBinding.root)
        dBinding.tvTitle.text = getString(R.string.titleAddUserToList)

        dBinding.rvList.layoutManager = LinearLayoutManager(this)

        val adapter = AddUserToGroupAdapter(this, userList)
        dBinding.rvList.adapter = adapter
        mAddUserToGroupDialog.show()

        /*val groupRef = FirebaseDatabase.getInstance().getReference("Groups")
        val groupNameRef = groupRef.child(groupName!!)
        val groupMembersRef = groupNameRef.child("Members")*/

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    val uid = currentUser!!.uid.toString()
                    //Log.i("UserIDForAddToGroup", "$uid")
                    if (mAuth.currentUser!!.uid != uid ) {
                        userList.add(currentUser)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setUpToolbarContent(){
        setupToolbar()
        val name = intent.getStringExtra("name")
        binding?.tvProfileName?.text = name
    }

    private fun setupToolbar() {
        setSupportActionBar(binding?.toolbarChatActivity)
        val actionBar = supportActionBar
//        binding?.toolbarChatActivity?.inflateMenu(R.menu.menu_chat)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        binding?.toolbarChatActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}