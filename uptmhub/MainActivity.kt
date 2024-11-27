package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    //var = variable
    private lateinit var btnlogin: Button
    private lateinit var btnsingup: Button
    private lateinit var buttontry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        btnlogin = findViewById<Button>(R.id.loginbtn)
        btnsingup = findViewById<Button>(R.id.signupbtn)
        buttontry = findViewById(R.id.btntryview)

        buttontry.setOnClickListener {
            val i = Intent (this, AdminInterface::class.java)
            startActivity(i)
        }

        btnlogin.setOnClickListener {
            val i = Intent (this, loginPage::class.java)
            startActivity(i)
        }

        btnsingup.setOnClickListener {
            val i = Intent (this, signUpPage::class.java)
            startActivity(i)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}