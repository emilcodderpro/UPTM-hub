package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class itemSuccessPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_success_page)

        // Set up a button to go back to the main page or to upload more items
        val backToHomeButton: Button = findViewById(R.id.btnBackToHome)
        backToHomeButton.setOnClickListener {
            // Navigate to the main page (or another page of your choice)
            val mainIntent = Intent(this, homePage::class.java) // Adjust if your main page is different
            startActivity(mainIntent)
            finish()
        }
    }
}
