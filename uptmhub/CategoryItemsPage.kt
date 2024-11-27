package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CategoryItemsPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemList: ArrayList<ItemModel>
    private lateinit var adapter: ItemAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_items_page)

        // Retrieve the category name passed from the previous activity
        val categoryName = intent.getStringExtra("CATEGORY") ?: "Unknown Category"

        // Set the category name in the TextView
        val categoryTextView: TextView = findViewById(R.id.categoryTextView)
        categoryTextView.text = categoryName

        // Get the category from the intent
        category = intent.getStringExtra("CATEGORY") ?: ""

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        itemList = ArrayList()

        // Initialize adapter
        adapter = ItemAdapter(itemList) { itemId ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("ITEM_ID", itemId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Fetch and filter items based on the category
        fetchItemsByCategory()
    }


    private fun fetchItemsByCategory() {
        dbRef = FirebaseDatabase.getInstance().getReference("Items")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemModel::class.java)
                    if (item != null && item.category == category) {
                        itemList.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoryItemsPage", "Database error: ${error.message}")
            }
        })
    }
}
