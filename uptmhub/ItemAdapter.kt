package com.example.uptmhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ItemAdapter(
    private val itemList: List<ItemModel>,
    private val onItemClick: (String) -> Unit // Function to handle item click, receiving item ID

) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.ItemImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)

        init {
            // Set click listener to pass the item ID to the activity
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val itemId = itemList[position].itemId
                    if (itemId != null) {
                        onItemClick(itemId)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemName.text = currentItem.name
        holder.itemPrice.text = "RM ${currentItem.price}"

        // Load the image from Firebase Storage URL into the ImageView
        val imageUrl = currentItem.imageUrl
        Glide.with(holder.itemView.context)
            .load(imageUrl) // Image URL from Firebase Storage
            .into(holder.itemImage) // Set the image into the ImageView
    }

    override fun getItemCount(): Int = itemList.size
}
