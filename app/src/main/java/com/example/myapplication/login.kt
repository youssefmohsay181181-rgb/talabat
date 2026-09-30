package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Disable button to prevent double-clicks
                binding.loginButton.isEnabled = false

                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        binding.loginButton.isEnabled = true

                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                            // Redirect directly to Profile Activity so user can edit profile
                            val intent = Intent(this, profile::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Displays toast when email or password is wrong
                            Toast.makeText(
                                this,
                                "Login Unsuccessful: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Please enter the email and password you registered with",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.signupText.setOnClickListener {
            startActivity(Intent(this, register::class.java))
            finish()
        }
    }
}