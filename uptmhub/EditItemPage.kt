package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditItemPage : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var itemName: EditText
    private lateinit var price: EditText
    private lateinit var description: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var size: EditText
    private lateinit var color: EditText
    private lateinit var itemId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_item_page)

        // Retrieve item ID and user ID passed from previous activity
        itemId = intent.getStringExtra("ITEM_ID") ?: ""
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("USER_ID", null).toString()

        if (userId.isEmpty() || itemId.isEmpty()) {
            Toast.makeText(this, "User or item ID not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize components
        submitButton = findViewById(R.id.btnSubmit)
        deleteButton = findViewById(R.id.btnDelete)
        itemName = findViewById(R.id.etItemName)
        price = findViewById(R.id.etPrice)
        description = findViewById(R.id.etDescription)
        categorySpinner = findViewById(R.id.spinnerCategory)
        size = findViewById(R.id.etSize)
        color = findViewById(R.id.etColor)

        // Initialize database reference
        dbRef = FirebaseDatabase.getInstance().getReference("Items")

        // Set up the category spinner
        val categories = arrayOf("Food", "Item", "Rental Place", "Car Rental")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Load item details from Firebase
        loadItemDetails()

        // Handle submit button click for updating item
        submitButton.setOnClickListener {
            if (itemName.text.isEmpty() || price.text.isEmpty() || description.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Gather updated item details
                val updatedCategory = categorySpinner.selectedItem.toString()
                val updatedSizes = size.text.toString().split(",").map { it.trim() }
                val updatedColors = color.text.toString().split(",").map { it.trim() }
                val updatedPrice = price.text.toString().toDouble()

                // Call function to update the item in Firebase
                updateItem(
                    itemName.text.toString(),
                    updatedPrice,
                    description.text.toString(),
                    updatedCategory,
                    updatedSizes,
                    updatedColors
                )
            }
        }

        // Handle delete button click for deleting item
        deleteButton.setOnClickListener {
            deleteItem()
        }
    }

    private fun loadItemDetails() {
        dbRef.child(itemId).get().addOnSuccessListener { snapshot ->
            val item = snapshot.getValue(ItemModel::class.java)
            if (item != null) {
                // Populate fields with item data
                itemName.setText(item.name)
                price.setText(item.price.toString())
                description.setText(item.description)
                size.setText(item.sizes.joinToString(", "))
                color.setText(item.colors.joinToString(", "))

                // Set selected category in spinner
                val categoryIndex = (categorySpinner.adapter as ArrayAdapter<String>).getPosition(item.category)
                categorySpinner.setSelection(categoryIndex)
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load item", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateItem(
        name: String,
        price: Double,
        description: String,
        category: String,
        sizes: List<String>,
        colors: List<String>
    ) {
        val updatedItem = ItemModel(name, price, description, category, itemId, sizes, colors, userId)

        dbRef.child(itemId).setValue(updatedItem).addOnCompleteListener {
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_LONG).show()
            finish()  // Close the activity and return to previous screen
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update item", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteItem() {
        dbRef.child(itemId).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_LONG).show()
            finish()  // Close the activity and return to previous screen
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete item", Toast.LENGTH_LONG).show()
        }
    }
}
