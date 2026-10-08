package com.example.sellerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Connect to Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Connect Kotlin variables to the UI
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Get the currently logged-in user
        val currentUser = auth.currentUser

        // Make sure a user is actually logged in
        if (currentUser == null) {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            return
        }

        // Get the user's unique Firebase UID
        val userId = currentUser.uid

        // Reference to this user's profile
        val userReference = database.reference
            .child("users")
            .child(userId)

        // Get the user's profile from Realtime Database
        userReference.get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {

                    val name = snapshot.child("name").value?.toString() ?: ""
                    val address = snapshot.child("address").value?.toString() ?: ""
                    val phone = snapshot.child("phone").value?.toString() ?: ""

                    // Display the data in the EditTexts
                    nameEditText.setText(name)
                    addressEditText.setText(address)
                    phoneEditText.setText(phone)

                } else {

                    Toast.makeText(
                        this,
                        "No profile data found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to load profile",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Update profile when the user presses the button
        updateButton.setOnClickListener {

            val name = nameEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val updatedProfile = mapOf(
                "role" to "seller",
                "name" to name,
                "address" to address,
                "phone" to phone
            )

            userReference.setValue(updatedProfile)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Profile updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Failed to update profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        // Log out
        logoutButton.setOnClickListener {

            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}