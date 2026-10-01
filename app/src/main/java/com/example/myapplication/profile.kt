package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class profile : AppCompatActivity() {

    private lateinit var mUserName: EditText
    private lateinit var mUserPhone: EditText
    private lateinit var mUpdateBtn: Button
    private lateinit var mSkipBtn: Button
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mUserName = findViewById(R.id.userProfileName)
        mUserPhone = findViewById(R.id.userProfilePhone)
        mUpdateBtn = findViewById(R.id.userProfileBtn)
        mSkipBtn = findViewById(R.id.userSkipBtn)

        mAuth = FirebaseAuth.getInstance()
        val currentFirebaseUser = mAuth.currentUser

        if (currentFirebaseUser != null) {
            val uid = currentFirebaseUser.uid
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid)

            // Read existing profile data from Realtime Database
            getUserData()
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Save / Edit profile
        mUpdateBtn.setOnClickListener {
            val name = mUserName.text.toString().trim()
            val phone = mUserPhone.text.toString().trim()

            if (TextUtils.isEmpty(name)) {
                mUserName.error = "Enter name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(phone)) {
                mUserPhone.error = "Enter phone number"
                return@setOnClickListener
            }
            updateUser(name, phone)
        }

        // Skip / Proceed to Hello World (MainActivity) without editing
        mSkipBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getUserData() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: ""
                    val phone = snapshot.child("phone").value?.toString() ?: ""

                    mUserName.setText(name)
                    mUserPhone.setText(phone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@profile, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUser(name: String, phone: String) {
        val userMap = HashMap<String, String>()
        userMap["name"] = name
        userMap["phone"] = phone

        mDatabase.setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}