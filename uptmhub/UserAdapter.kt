package com.example.uptmhub

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class UserAdapter(private val userList: MutableList<Model>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvUserName)
        val email: TextView = itemView.findViewById(R.id.tvUserEmail)
        val phone: TextView = itemView.findViewById(R.id.tvUserPhone)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete)
        val userEdit: ImageView = itemView.findViewById(R.id.editUser)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.name.text = currentUser.userName ?: "No Name"
        holder.email.text = currentUser.userEmail ?: "No Email"
        holder.phone.text = currentUser.userPhone ?: "No Phone"

        holder.userEdit.setOnClickListener {
            val context = holder.itemView.context // Get the correct context
            val intent = Intent(context, EditUserPage::class.java)

            // Pass user data to EditUserPage
            intent.putExtra("userId", currentUser.userId)
            intent.putExtra("userName", currentUser.userName)
            intent.putExtra("userEmail", currentUser.userEmail)
            intent.putExtra("userPhone", currentUser.userPhone)

            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val context = holder.itemView.context
            val userId = currentUser.userId  // Ensure `userId` is part of the `Model` class

            // Show confirmation dialog
            AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes") { dialog, _ ->
                    FirebaseDatabase.getInstance().getReference("user").child(userId!!).removeValue()
                        .addOnSuccessListener {
                            userList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(context, "Failed to delete: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }



    override fun getItemCount(): Int = userList.size
}

