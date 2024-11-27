package com.example.uptmhub

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class ItemViewAdapter(
    private val itemList: MutableList<ItemModel>,
    private val context: Context
) : RecyclerView.Adapter<ItemViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvItemName)
        val price: TextView = itemView.findViewById(R.id.tvItemPrice)
        val category: TextView = itemView.findViewById(R.id.tvItemCategory)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.name.text = currentItem.name
        holder.price.text = "$${currentItem.price}"
        holder.category.text = currentItem.category

        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(currentItem, position)
        }
    }

    override fun getItemCount(): Int = itemList.size

    private fun showDeleteConfirmationDialog(item: ItemModel, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete the item '${item.name}'?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteItemFromDatabase(item, position)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteItemFromDatabase(item: ItemModel, position: Int) {
        FirebaseDatabase.getInstance().getReference("Items").child(item.itemId).removeValue()
            .addOnSuccessListener {
                itemList.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(context, "Item deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Failed to delete item: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
