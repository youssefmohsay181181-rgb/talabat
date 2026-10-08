package com.example.sellerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Connect to Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Connect to Firebase Realtime Database
        database = FirebaseDatabase.getInstance()

        // Connect Kotlin variables to the UI elements
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)

        // What happens when the user presses Sign Up
        signupButton.setOnClickListener {

            val name = nameEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // Make sure all fields are filled
            if (name.isEmpty() ||
                address.isEmpty() ||
                phone.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Create the user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        // Get the UID Firebase created for this user
                        val userId = auth.currentUser!!.uid

                        // Create the user's profile data
                        val userProfile = mapOf(
                            "role" to "seller",
                            "name" to name,
                            "address" to address,
                            "phone" to phone
                        )

                        // Store the profile under the user's UID
                        database.reference
                            .child("users")
                            .child(userId)
                            .setValue(userProfile)
                            .addOnCompleteListener { databaseTask ->

                                if (databaseTask.isSuccessful) {

                                    Toast.makeText(
                                        this,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Open the profile page
                                    val intent = Intent(
                                        this,
                                        ProfileActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()

                                } else {

                                    Toast.makeText(
                                        this,
                                        "Account created, but profile could not be saved",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                    } else {

                        Toast.makeText(
                            this,
                            "Sign up failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // Go back to the Login page
        loginTextView.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}