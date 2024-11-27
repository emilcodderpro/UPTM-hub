package com.example.uptmhub

data class ItemModel(
    val name: String = "",
    val price: Double = 0.00,
    val description: String = "",
    val category: String ="",
    val itemId: String = "",
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val imageUrl: String = "",
    var userId: String? = null // Add this field

)
