package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class profile : AppCompatActivity() {

    lateinit var mUserName: EditText
    lateinit var mUserStatus: EditText
    lateinit var mUpdateBtn: Button
    lateinit var mDatabase: DatabaseReference
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mUserName = findViewById(R.id.userProfileName)
        mUserStatus = findViewById(R.id.userProfilePhone)
        mUpdateBtn = findViewById(R.id.userProfileBtn)

        mAuth = FirebaseAuth.getInstance()
        val currentFirebaseUser = mAuth.currentUser

        if (currentFirebaseUser != null) {
            val uid = currentFirebaseUser.uid
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mUpdateBtn.setOnClickListener {
            val name = mUserName.text.toString().trim()
            val status = mUserStatus.text.toString().trim()

            if (TextUtils.isEmpty(name)) {
                mUserName.error = "Enter name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(status)) {
                mUserStatus.error = "Enter status"
                return@setOnClickListener
            }
            updateUser(name, status)
        }
    }

    private fun updateUser(name: String, status: String) {
        val userMap = HashMap<String, String>()
        userMap["name"] = name
        userMap["status"] = status

        mDatabase.setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}