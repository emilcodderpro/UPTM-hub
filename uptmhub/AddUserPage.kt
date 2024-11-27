package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddUserPage : AppCompatActivity() {

    // Firebase Database reference
    private lateinit var dbRef: DatabaseReference

    // UI Components
    private lateinit var submitButton: Button
    private lateinit var nameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var phoneField: EditText
    private lateinit var emailField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_page)

        // Initialize UI Components
        submitButton = findViewById(R.id.btnSubmitUser)
        nameField = findViewById(R.id.etUserName)
        passwordField = findViewById(R.id.etUserPassword)
        phoneField = findViewById(R.id.etUserPhone)
        emailField = findViewById(R.id.etUserEmail)

        // Initialize Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        // Submit button click listener
        submitButton.setOnClickListener {
            // Validate input fields
            val name = nameField.text.toString()
            val password = passwordField.text.toString()
            val phone = phoneField.text.toString()
            val email = emailField.text.toString()

            if (name.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Save the new user data
                saveUser(name, password, phone, email)
            }
        }
    }

    // Function to save user data to Firebase
    private fun saveUser(name: String, password: String, phone: String, email: String) {
        val userId = dbRef.push().key!! // Generate unique user ID

        // Create user model
        val user = Model(email, userId, name, password, phone)

        // Save data to Firebase
        dbRef.child(userId).setValue(user)
            .addOnCompleteListener {
                Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to success page
                val successIntent = Intent(this, AdminInterface::class.java)
                startActivity(successIntent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
            }
    }
}


