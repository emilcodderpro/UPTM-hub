package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ProfilePage : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayList<ItemModel>
    private lateinit var adapter: ItemAdapter
    private lateinit var logOutAct: ImageView
    private lateinit var buttonEditProfile: Button
    private lateinit var buttonViewUser: Button
    private lateinit var buttonBackk : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        nameTextView = findViewById(R.id.userNameText)
        emailTextView = findViewById(R.id.userEmailText)
        recyclerView = findViewById(R.id.recyclerView)
        buttonEditProfile = findViewById(R.id.btnEditProfile)
        buttonViewUser = findViewById(R.id.btnviewuser)
        buttonBackk = findViewById(R.id.btnbackk)

        buttonBackk.setOnClickListener {
            val i = Intent (this, homePage::class.java)
            startActivity(i)
        }

        buttonViewUser.setOnClickListener {
            val i = Intent (this, UserViewActivity::class.java)
            startActivity(i)
        }

        buttonEditProfile.setOnClickListener {
            val i = Intent (this, EditProfilePage::class.java)
            startActivity(i)
        }


        // Initialize the logout button
        logOutAct = findViewById(R.id.logoutbtn)
        logOutAct.setOnClickListener {
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

        // Set up RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        itemList = ArrayList()
        adapter = ItemAdapter(itemList) { itemId ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("ITEM_ID", itemId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Retrieve the user ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID", null)

        if (userId != null) {
            fetchUserData(userId)  // Fetch user data
            fetchItemsFromDatabase(userId)  // Fetch user-specific items
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserData(userId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

        dbRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(Model::class.java)
            if (user != null) {
                nameTextView.text = user.userName
                emailTextView.text = user.userEmail
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch items uploaded by the logged-in user based on user ID
    private fun fetchItemsFromDatabase(userId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Items")

        // Query Firebase to get items by user ID
        dbRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
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
                    Toast.makeText(this@ProfilePage, "Failed to load items", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
