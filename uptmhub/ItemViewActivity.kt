package com.example.uptmhub

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ItemViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemViewAdapter
    private lateinit var itemList: ArrayList<ItemModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view)

        recyclerView = findViewById(R.id.itemRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        itemList = ArrayList()
        itemAdapter = ItemViewAdapter(itemList, this)
        recyclerView.adapter = itemAdapter

        fetchItemsFromDatabase()
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
                if (itemList.isEmpty()) {
                    Log.d("ItemViewActivity", "No items found")
                }
                itemAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ItemViewActivity", "Database error: ${error.message}")
            }
        })
    }
}
