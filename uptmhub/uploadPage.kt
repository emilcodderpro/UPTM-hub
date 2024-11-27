package com.example.uptmhub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class uploadPage : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private lateinit var submit: Button
    private lateinit var imageselect: Button
    private lateinit var itemName: EditText
    private lateinit var price: EditText
    private lateinit var description: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var size: EditText
    private lateinit var color: EditText
    private var imageUri: Uri? = null
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_page)

        // Retrieve user ID passed from loginPage
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userId = sharedPreferences.getString("USER_ID", null).toString()
        if (userId.isEmpty()) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize Firebase references
        dbRef = FirebaseDatabase.getInstance().getReference("Items")
        storageRef = FirebaseStorage.getInstance()

        // Initialize UI components
        submit = findViewById(R.id.btnSubmit)
        imageselect = findViewById(R.id.btnSelectImage)
        itemName = findViewById(R.id.etItemName2)
        price = findViewById(R.id.etPrice)
        description = findViewById(R.id.etDescription)
        categorySpinner = findViewById(R.id.spinnerCategory)
        size = findViewById(R.id.etSize)
        color = findViewById(R.id.etColor)

        // Set up category spinner
        val categories = arrayOf("Food", "Item", "Rental Place", "Car Rental")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Image selection button click listener
        imageselect.setOnClickListener { pickImageFromGallery() }

        // Submit button click listener
        submit.setOnClickListener {
            if (itemName.text.isEmpty() || price.text.isEmpty() || description.text.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            } else {
                val selectedCategory = categorySpinner.selectedItem.toString()
                val availableSizes = size.text.toString().split(",").map { it.trim() }
                val availableColors = color.text.toString().split(",").map { it.trim() }

                uploadImageAndSaveData(
                    itemName.text.toString(),
                    price.text.toString().toDouble(),
                    description.text.toString(),
                    selectedCategory,
                    availableSizes,
                    availableColors,
                    userId
                )
            }
        }
    }

    // Launch gallery to pick an image
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Handle result from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Upload image to Firebase Storage and save item data
    private fun uploadImageAndSaveData(
        name: String,
        price: Double,
        description: String,
        category: String,
        sizes: List<String>,
        colors: List<String>,
        userId: String
    ) {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = UUID.randomUUID().toString() // Generate unique file name
        val storageReference = storageRef.reference.child("ItemImages/$fileName")

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    saveData(name, price, description, category, sizes, colors, userId, uri.toString())
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Save item data to Firebase Database
    private fun saveData(
        name: String,
        price: Double,
        description: String,
        category: String,
        sizes: List<String>,
        colors: List<String>,
        userId: String,
        imageUrl: String
    ) {
        val itemId = dbRef.push().key ?: return

        val item = ItemModel(name, price, description, category, itemId, sizes, colors, imageUrl, userId)

        dbRef.child(itemId).setValue(item)
            .addOnCompleteListener {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                goToSuccessPage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }

    // Navigate to success page
    private fun goToSuccessPage() {
        val successIntent = Intent(this, itemSuccessPage::class.java)
        startActivity(successIntent)
        finish()
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
