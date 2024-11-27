package com.example.uptmhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SearchResultPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchResults: ArrayList<ItemModel>
    private lateinit var adapter: ItemAdapter
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result_page)

        val query = intent.getStringExtra("QUERY") ?: ""
        recyclerView = findViewById(R.id.recyclerViewSearchResult)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        searchResults = ArrayList()
        adapter = ItemAdapter(searchResults) { itemId ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("ITEM_ID", itemId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        searchItemsInDatabase(query)
    }

    private fun searchItemsInDatabase(query: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Items")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                searchResults.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemModel::class.java)
                    if (item != null && item.name.contains(query, ignoreCase = true)) {
                        searchResults.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SearchResultPage", "Database error: ${error.message}")
            }
        })
    }
}
