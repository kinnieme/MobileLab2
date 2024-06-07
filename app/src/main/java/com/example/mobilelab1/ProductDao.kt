package com.example.mobilelab1

import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): List<ProductEntity>

    @Insert
    fun insertProduct(product: ProductEntity)

    @Update
    fun updateProduct(product: ProductEntity)

    @Delete
    fun deleteProduct(product: ProductEntity)
}
