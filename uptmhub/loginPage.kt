package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class loginPage : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        // Initialize UI elements
        emailField = findViewById(R.id.etEmailLogin)
        passwordField = findViewById(R.id.etPasswordLogin)
        loginButton = findViewById(R.id.btnLogin)

        // Reference to the "user" node in Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("user")

        // Set up login button click listener
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim().lowercase() // Normalize email to lowercase
            val password = passwordField.text.toString().trim()

            // Check for empty fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Admin credentials check
                if (email == "admininterface@uptmhub.com" && password == "password") {
                    Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AdminInterface::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Regular user login check
                    checkLogin(email, password)
                }
            }

        }
    }

    // Function to check if the user credentials are valid
    private fun checkLogin(email: String, password: String) {
        // Query the database for the user with the provided email
        dbRef.orderByChild("userEmail").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var isPasswordCorrect = false
                        for (userSnapshot in snapshot.children) {
                            // Retrieve the user data using the Model class
                            val user = userSnapshot.getValue(Model::class.java)

                            // Log the user email and password to check what is fetched
                            Log.d("LoginPage", "Fetched email: ${user?.userEmail}")
                            Log.d("LoginPage", "Fetched password: ${user?.userPassword}")

                            // Compare the password from the model class
                            if (user?.userPassword == password) {
                                isPasswordCorrect = true
                                Toast.makeText(this@loginPage, "Login successful", Toast.LENGTH_LONG).show()

                                // Save the user ID in SharedPreferences
                                val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("USER_ID", user.userId)
                                editor.apply()

                                // Navigate to home page on successful login
                                val i = Intent(this@loginPage, homePage::class.java)
                                startActivity(i)
                                finish() // Close login activity
                                break
                            }
                        }
                        if (!isPasswordCorrect) {
                            Toast.makeText(this@loginPage, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@loginPage, "Email not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@loginPage, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
