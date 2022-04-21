package com.example.firebasechatapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.firebasechatapp.databinding.ActivitySignupBinding
import com.example.firebasechatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : BaseActivity() {

    private var binding: ActivitySignupBinding? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mAuth = FirebaseAuth.getInstance()

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

        binding?.btnSignup?.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name: String = binding?.etName?.text.toString().trim{ it <= ' '}
        val email:String = binding?.etEmail?.text.toString().trim{ it <= ' '}
        val password:String = binding?.etPassword?.text.toString().trim{ it <= ' '}

        if(validateForm(name, email, password)) {
            showProgressDialog("Please Wait until you signup!!!")
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        addUserToDataBase(mAuth.currentUser?.uid!!, name, email)
                        hideProgressDialog()
                        val intent = Intent(this@SignupActivity, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun addUserToDataBase(uid: String,name: String, email: String ) {
        mDbRef = FirebaseDatabase.getInstance().reference
        mDbRef.child("user").child(uid).setValue(User(uid,name,email))
    }

    private fun validateForm(name: String,email: String, password: String ): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password")
                false
            }else -> {
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}