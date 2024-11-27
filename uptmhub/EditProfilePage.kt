package com.example.uptmhub

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class EditProfilePage : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var saveButton: Button
    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page)

        // Initialize UI components
        saveButton = findViewById(R.id.btnSave)
        name = findViewById(R.id.etNameEdit)
        password = findViewById(R.id.etPasswordEdit)
        phone = findViewById(R.id.etPhoneEdit)
        email = findViewById(R.id.etEmailEdit)

        // Retrieve userId from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("USER_ID", null)

        // Load data if userId is available
        if (userId != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("user").child(userId!!)
            loadData()
            saveButton.setOnClickListener { updateData() }
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Load current data from Firebase
    private fun loadData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Model::class.java)
                    user?.let {
                        name.setText(it.userName)
                        password.setText(it.userPassword)
                        phone.setText(it.userPhone)
                        email.setText(it.userEmail)
                    }
                } else {
                    Toast.makeText(this@EditProfilePage, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfilePage, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Update data in Firebase
    private fun updateData() {
        val updatedName = name.text.toString()
        val updatedPassword = password.text.toString()
        val updatedPhone = phone.text.toString()
        val updatedEmail = email.text.toString()

        if (updatedName.isEmpty() || updatedPassword.isEmpty() || updatedPhone.isEmpty() || updatedEmail.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Update user data in Firebase
        val updatedUser = Model(updatedEmail, userId!!, updatedName, updatedPassword, updatedPhone)

        dbRef.setValue(updatedUser)
            .addOnCompleteListener {
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_LONG).show()

                // Redirect to ProfilePage after updating
                val intent = Intent(this, ProfilePage::class.java)
                startActivity(intent)
                finish()  // Close the current activity
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_LONG).show()
            }
    }
}
