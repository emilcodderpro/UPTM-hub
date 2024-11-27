package com.example.uptmhub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class EditUserPage : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: EditText
    private lateinit var userPhoneEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_page)

        // Initialize views
        userNameEditText = findViewById(R.id.editTextUserName)
        userEmailEditText = findViewById(R.id.editTextUserEmail)
        userPhoneEditText = findViewById(R.id.editTextUserPhone)
        saveButton = findViewById(R.id.btnSave)

        // Retrieve user data from Intent
        userId = intent.getStringExtra("userId") ?: ""
        val userName = intent.getStringExtra("userName")
        val userEmail = intent.getStringExtra("userEmail")
        val userPhone = intent.getStringExtra("userPhone")

        // Populate fields with user data
        userNameEditText.setText(userName)
        userEmailEditText.setText(userEmail)
        userPhoneEditText.setText(userPhone)

        // Set up the save button
        saveButton.setOnClickListener {
            val updatedUserName = userNameEditText.text.toString()
            val updatedUserEmail = userEmailEditText.text.toString()
            val updatedUserPhone = userPhoneEditText.text.toString()

            if (updatedUserName.isNotEmpty() && updatedUserEmail.isNotEmpty() && updatedUserPhone.isNotEmpty()) {
                // Update the user data in Firebase
                val userUpdates = mapOf(
                    "userName" to updatedUserName,
                    "userEmail" to updatedUserEmail,
                    "userPhone" to updatedUserPhone
                )

                FirebaseDatabase.getInstance().getReference("user").child(userId)
                    .updateChildren(userUpdates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show()
                        finish() // Go back to the previous page after saving
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Failed to update: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
