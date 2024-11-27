package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class homePage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayList<ItemModel>
    private lateinit var adapter: ItemAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var searchBar: EditText
    private lateinit var buttonItem: ImageView
    private lateinit var buttonFood: ImageView
    private lateinit var buttonRentalPlace: ImageView
    private lateinit var buttonCarRental: ImageView
    private lateinit var buttonProfile: ImageView
    private lateinit var buttonUpload: ImageView
    private lateinit var userName: TextView
    private lateinit var logoutbtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        itemList = ArrayList()
        adapter = ItemAdapter(itemList) { itemId ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("ITEM_ID", itemId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        searchBar = findViewById(R.id.editTextText)
        buttonItem = findViewById(R.id.btnitem)
        buttonFood = findViewById(R.id.btnFood)
        buttonUpload = findViewById(R.id.btnUploaditem)
        userName = findViewById(R.id.displayUserName)
        logoutbtn = findViewById(R.id.btnlogout)

        searchBar.setOnEditorActionListener { _, _, _ ->
            val query = searchBar.text.toString().trim()
            if (query.isNotEmpty()) {
                val intent = Intent(this, SearchResultPage::class.java)
                intent.putExtra("QUERY", query)
                startActivity(intent)
            }
            true
        }

        logoutbtn.setOnClickListener {
            // Clear user data in SharedPreferences
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            sharedPreferences.edit().remove("USER_ID").apply()

            // Show a message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Redirect to login activity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close the current activity
        }

        buttonUpload.setOnClickListener {
            val intent = Intent(this, uploadPage::class.java)
            startActivity(intent)
        }

        buttonProfile = findViewById(R.id.btnProfile)

        buttonProfile.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
        }

        buttonItem.setOnClickListener {
            val intent = Intent(this, CategoryItemsPage::class.java)
            intent.putExtra("CATEGORY", "Item")
            startActivity(intent)
        }

        buttonFood.setOnClickListener {
            val intent = Intent(this, CategoryItemsPage::class.java)
            intent.putExtra("CATEGORY", "Food")
            startActivity(intent)
        }

        buttonRentalPlace = findViewById(R.id.btnRentalPlace)

        buttonRentalPlace.setOnClickListener {
            val intent = Intent(this, CategoryItemsPage::class.java)
            intent.putExtra("CATEGORY", "Rental Place")
            startActivity(intent)
        }

        buttonCarRental = findViewById(R.id.btnCarRental)

        buttonCarRental.setOnClickListener {
            val intent = Intent(this, CategoryItemsPage::class.java)
            intent.putExtra("CATEGORY", "Car Rental")
            startActivity(intent)
        }

        fetchItemsFromDatabase()

        // Retrieve user ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID", null)

        if (userId != null) {
            fetchUserName(userId)
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchItemsFromDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Items")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemModel::class.java)
                    if (item != null) {
                        itemList.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@homePage, "Failed to load items", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserName(userId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

        dbRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(Model::class.java)
            if (user != null) {
                userName.text = user.userName
            } else {
                userName.text = "No Name"
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            userName.text = "No Name"
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
        }
    }
}
