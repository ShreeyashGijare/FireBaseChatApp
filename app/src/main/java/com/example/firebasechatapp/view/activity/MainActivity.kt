package com.example.firebasechatapp.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.R
import com.example.firebasechatapp.databinding.ActivityMainBinding
import com.example.firebasechatapp.model.Group
import com.example.firebasechatapp.model.User
import com.example.firebasechatapp.view.adapter.GroupAdapter
import com.example.firebasechatapp.view.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.protobuf.Value

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var userList: ArrayList<User> = ArrayList()
    private var groupList: ArrayList<Group> = ArrayList()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    var groupName: String? = null

    private lateinit var mGroupsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //LateInit var Initialization
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        //Code to remove StatusBar
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            @Suppress("DEPRECATION")
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }

        setupToolbar()
        setAdapter()
//        binding?.btnCreateGroup?.setOnClickListener{
//
//        }
    }

    private fun setAdapter() {

        //Single Chat Adapter
        val rvUserAdapter = binding?.rvUser
        rvUserAdapter?.layoutManager = LinearLayoutManager(this)
        val userAdapter = UserAdapter(this, userList)
        //val concatAdapter = ConcatAdapter(userAdapter, groupAdapter)
        rvUserAdapter?.adapter = userAdapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser!!.uid != currentUser!!.uid) {
                        userList.add(currentUser)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //Group Chat Adapter
        val rvGroupAdapter = binding?.rvGroup
        rvGroupAdapter?.layoutManager = LinearLayoutManager(this)
        val groupAdapter = GroupAdapter(this, groupList)
        rvGroupAdapter?.adapter = groupAdapter
        //Log.i("GlobalGroupMembersPath", "$mGroupsRef")

        //TODO Group display feature
        mDbRef.child("Groups").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentGroup = postSnapshot.key
                    //Log.i("CurrentGroup", "$currentGroup")
                    groupName = currentGroup.toString()
                    //groupList.add(Group(groupName))
                    //Log.i("groupName", "$groupName")
                    //TODO to get the list of all groups
                    mDbRef.child("Groups").child(groupName!!).child("Members").addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (memberId in snapshot.children) {
                                val memberUID = memberId.key.toString()
                                val mCurrentUser = mAuth.currentUser!!.uid
                                //Log.i("MemberUID", memberUID)
                                //Log.i("CurrentUID", mCurrentUser)
                                if (memberUID == mCurrentUser) {
                                    groupList.add(Group(currentGroup))
                                }
                                groupAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                    //groupAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//        groupAdapter.notifyDataSetChanged()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding?.toolbarMainActivity)
        binding?.toolbarMainActivity?.inflateMenu(R.menu.menu)
        val actionBar = supportActionBar
        actionBar?.title = "Chat"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        if(item.itemId == R.id.createGroup) {
            val input = EditText(this)
            AlertDialog.Builder(this).apply {
                setTitle("Create Group:")
                setView(input)
                setPositiveButton("Ok") { _, _ ->
                    val textInput = input.text.toString()
                    if (textInput.isNotBlank() && textInput.length <= 40) {
                        createNewGroup(textInput)
                        mDbRef.child("Groups").child(textInput).child("Members").child(mAuth.currentUser!!.uid).setValue(Group(null,mAuth.currentUser!!.uid))
                        //"lzXItvgEpCOlltAnLWVEe6X9r022"
                        mDbRef.child("user").child(mAuth.currentUser!!.uid).child("InWhichGroup").push().setValue(User(groupName = textInput))
                    }
                }
                setNegativeButton("Cancel") { _, _ -> }
                show()
            }
        }
        return true
    }

    private fun createNewGroup(groupName: String){
        mDbRef.child("Groups").child(groupName)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}