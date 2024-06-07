package com.example.mobilelab1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var manufacturer: String,
    var year: Int,
    var quantity: Int,
    var price: Double
)
