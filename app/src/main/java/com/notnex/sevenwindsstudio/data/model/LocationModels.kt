package com.notnex.sevenwindsstudio.data.model

data class Point(
    val latitude: Double,
    val longitude: Double
)

data class Location(
    val id: Int,
    val name: String,
    val point: Point,
    val distance: Double? = null
)

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Int,
    val imageURL: String? = null
)

data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int
) 