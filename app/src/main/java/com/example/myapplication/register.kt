package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            // Prevent multiple rapid clicks
            binding.signupButton.isEnabled = false

            val name = binding.signupName.text.toString().trim()
            val status = binding.signupStatus.text.toString().trim()
            val email = binding.signupEmail.text.toString().trim()
            val password = binding.signupPassword.text.toString().trim()

            // Field validation
            if (name.isEmpty()) {
                binding.signupName.error = "Enter name"
                binding.signupButton.isEnabled = true
                return@setOnClickListener
            }
            if (status.isEmpty()) {
                binding.signupStatus.error = "Enter status"
                binding.signupButton.isEnabled = true
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.signupEmail.error = "Enter email"
                binding.signupButton.isEnabled = true
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.signupPassword.error = "Enter password"
                binding.signupButton.isEnabled = true
                return@setOnClickListener
            }

            // Step 1: Create account in Firebase Auth
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = firebaseAuth.currentUser?.uid

                        if (uid != null) {
                            val userMap = HashMap<String, String>()
                            userMap["name"] = name
                            userMap["status"] = status

                            // Step 2: Post immediately to Realtime Database under Users/{uid}
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .setValue(userMap)
                                .addOnCompleteListener { dbTask ->
                                    binding.signupButton.isEnabled = true
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                                        // Step 3: Redirect directly to Profile Activity
                                        val intent = Intent(this, profile::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Database Write Failed: ${dbTask.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            binding.signupButton.isEnabled = true
                        }
                    } else {
                        binding.signupButton.isEnabled = true
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "This email is already registered. Please log in.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, login::class.java))
            finish()
        }
    }
}