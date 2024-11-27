package com.example.uptmhub

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class UserViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<Model>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_view)

        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        userList = ArrayList()
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        fetchUsersFromDatabase()
    }

    private fun fetchUsersFromDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("user")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Model::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                if (userList.isEmpty()) {
                    Log.d("UserViewActivity", "No users found")
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserViewActivity", "Database error: ${error.message}")
            }
        })
    }
}
