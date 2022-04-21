package com.example.firebasechatapp.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.firebasechatapp.R
import com.example.firebasechatapp.databinding.ActivityMainBinding
import com.example.firebasechatapp.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {

    private var binding: ActivitySettingsBinding? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //LateInit var Initialization
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        //Code to remove StatusBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        logoutUser()
        setupToolbar()
        editUserProfile()
    }

        private fun editUserProfile() {
            //To change name
            binding?.btnChangeImage?.setOnClickListener {
                Toast.makeText(this, "Add camera and gallery select functionality", Toast.LENGTH_SHORT).show()

            }
            //To change status
            binding?.btnChangeStatus?.setOnClickListener {
                val input = EditText(this)
                AlertDialog.Builder(this).apply {
                    setTitle("Status:")
                    setView(input)
                    setPositiveButton("Ok") { _, _ ->
                        val textInput = input.text.toString()
                        if (textInput.isNotBlank() && textInput.length <= 40) {
                            Log.i("UserStatus", textInput)
                        }
                    }
                    setNegativeButton("Cancel") { _, _ -> }
                    show()
                }
            }
        }

    private fun logoutUser(){
        binding?.btnLogout?.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding?.toolbarSettingActivity)
        val actionBar = supportActionBar
        actionBar?.title = "Settings"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        binding?.toolbarSettingActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}