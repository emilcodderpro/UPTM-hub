package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var itemRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var itemName: TextView
    private lateinit var itemDescription: TextView
    private lateinit var itemPrice: TextView
    private lateinit var contactInfo: TextView
    private lateinit var itemImage: ImageView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // Initialize views
        itemName = findViewById(R.id.itemNameDetail)
        itemDescription = findViewById(R.id.itemDescriptionDetail)
        itemPrice = findViewById(R.id.itemPriceDetail)
        itemImage = findViewById(R.id.itemImageDetail)
        backButton = findViewById(R.id.backToHomePageButton)
        contactInfo = findViewById(R.id.numContact)

        backButton.setOnClickListener {
            val i = Intent(this, homePage::class.java)
            startActivity(i)
        }

        // Get item ID from intent
        val itemId = intent.getStringExtra("ITEM_ID")
        if (itemId != null) {
            fetchItemDetails(itemId)
        } else {
            Log.e("ItemDetailActivity", "No item ID passed to detail activity.")
        }
    }

    private fun fetchItemDetails(itemId: String) {
        itemRef = FirebaseDatabase.getInstance().getReference("Items").child(itemId)
        itemRef.get().addOnSuccessListener { snapshot ->
            val item = snapshot.getValue(ItemModel::class.java)
            if (item != null) {
                // Display item details
                itemPrice.text = "RM${item.price}"
                itemName.text = item.name
                itemDescription.text = item.description

                // Load the item image using Glide
                Glide.with(this)
                    .load(item.imageUrl)
                    .into(itemImage)

                // Fetch the uploader's contact information
                fetchUploaderContact(item.userId.toString())
            }
        }.addOnFailureListener {
            Log.e("ItemDetailActivity", "Error fetching item details", it)
        }
    }

    private fun fetchUploaderContact(userId: String) {
        userRef = FirebaseDatabase.getInstance().getReference("user").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val phoneNumber = snapshot.child("userPhone").value as? String
            if (phoneNumber != null) {
                contactInfo.text = phoneNumber // Display the phone number
            } else {
                contactInfo.text = "No contact available"
            }
        }.addOnFailureListener {
            Log.e("ItemDetailActivity", "Error fetching user contact", it)
            contactInfo.text = "Error loading contact"
        }
    }
}
