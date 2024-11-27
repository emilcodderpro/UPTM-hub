package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class signUpPage : AppCompatActivity() {

    //declare to connect with database
    private lateinit var dbRef: DatabaseReference
    //initial all component
    private lateinit var submit: Button
    private lateinit var name : EditText
    private lateinit var password : EditText
    private lateinit var phone : EditText
    private lateinit var email : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)

        //declare all component
        submit = findViewById(R.id.btnSubmit2)
        name = findViewById(R.id.etName)
        password = findViewById(R.id.etPassword)
        phone = findViewById(R.id.etPhone)
        email = findViewById(R.id.etEmail)

        //pop up message when click button add record
        Toast.makeText(this, "Submit", Toast.LENGTH_LONG).show()

        submit.setOnClickListener {
            // Call function saveEmployeeData
            // Parameter - change the input data to string
            // Check if any fields are empty
            if (email.text.isEmpty() || name.text.isEmpty() || password.text.isEmpty() || phone.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // All fields are filled, proceed with saving data
                saveData(
                    email.text.toString(),
                    name.text.toString(),
                    password.text.toString(),
                    phone.text.toString()
                )
            }
        }
    }

    // Create the function saveData
    // This function sends data to Firebase
    // n = name
    // p = password
    // e = email
    // t = phone
    private fun saveData(e: String, n: String, p: String, t: String) {
        // Get instance = get object
        // Customer refers to table
        // Customer can change to other name
        // Link database named Customer
        val dbRef = FirebaseDatabase.getInstance().getReference("user")

        // Produce auto-generated customer ID
        // !!! Refer must have record or ID cannot be null
        val userId: String = dbRef.push().key!!

        // Customer is an object
        // Push the data to database
        // customerId will autogenerate
        // Data will output by user
        // Input name, password, phone, email
        val em = Model(e, userId, n, p, t)

        // Setting to push data inside table
        dbRef.child(userId).setValue(em)
            .addOnCompleteListener {
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

                // Go to the SuccessPage after successful sign-up
                val i = Intent(this, sucsessPage::class.java)
                startActivity(i)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
            }

        // Declare variable i to connect to next page/activity
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)

    }
}