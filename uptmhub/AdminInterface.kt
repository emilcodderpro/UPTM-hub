package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminInterface : AppCompatActivity() {

    private lateinit var buttonUserView: ImageView
    private lateinit var buttonItemView: ImageView
    private lateinit var buttonAddUser: ImageView
    private lateinit var buttonAddProduct: ImageView
    private lateinit var buttonExit: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_interface)

        buttonUserView = findViewById(R.id.btnViewUser)
        buttonItemView = findViewById(R.id.btnItemView)
        buttonAddUser = findViewById(R.id.btnAddUser)
        buttonAddProduct = findViewById(R.id.btnAddProduct)
        buttonExit = findViewById(R.id.btnExit)

        buttonExit.setOnClickListener {
            val i = Intent (this, MainActivity::class.java)
            startActivity(i)
        }

        buttonAddProduct.setOnClickListener {
            val i = Intent (this, AddProductPage::class.java)
            startActivity(i)
        }

        buttonAddUser.setOnClickListener {
            val i = Intent (this, AddUserPage::class.java)
            startActivity(i)
        }

        buttonItemView.setOnClickListener {
            val i = Intent (this, ItemViewActivity::class.java)
            startActivity(i)
        }

        buttonUserView.setOnClickListener {
            val i = Intent (this, UserViewActivity::class.java)
            startActivity(i)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}