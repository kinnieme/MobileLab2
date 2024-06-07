package com.example.mobilelab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val products = mutableListOf<ProductEntity>()
    private lateinit var adapter: ProductAdapter
    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        productDao = AppDatabase.getDatabase(this).productDao()

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val addButton: Button = findViewById(R.id.add_button)
        val calculateButton: Button = findViewById(R.id.calculate_button)

        adapter = ProductAdapter(products) { product ->
            showEditDialog(product)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            showAddDialog()
        }

        calculateButton.setOnClickListener {
            calculateTotalPriceForCurrentYear()
        }

        loadProducts()
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            val loadedProducts = withContext(Dispatchers.IO) {
                productDao.getAllProducts()
            }
            products.clear()
            products.addAll(loadedProducts)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.edit_name)
        val manufacturerEditText: EditText = dialogView.findViewById(R.id.edit_manufacturer)
        val yearEditText: EditText = dialogView.findViewById(R.id.edit_year)
        val quantityEditText: EditText = dialogView.findViewById(R.id.edit_quantity)
        val priceEditText: EditText = dialogView.findViewById(R.id.edit_price)

        AlertDialog.Builder(this)
            .setTitle("Добавить продукт")
            .setView(dialogView)
            .setPositiveButton("Добавить") { dialog, _ ->
                val name = nameEditText.text.toString()
                val manufacturer = manufacturerEditText.text.toString()
                val year = yearEditText.text.toString().toInt()
                val quantity = quantityEditText.text.toString().toInt()
                val price = priceEditText.text.toString().toDouble()
                val product = ProductEntity(name = name, manufacturer = manufacturer, year = year, quantity = quantity, price = price)
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        productDao.insertProduct(product)
                    }
                    loadProducts()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showEditDialog(product: ProductEntity) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.edit_name)
        val manufacturerEditText: EditText = dialogView.findViewById(R.id.edit_manufacturer)
        val yearEditText: EditText = dialogView.findViewById(R.id.edit_year)
        val quantityEditText: EditText = dialogView.findViewById(R.id.edit_quantity)
        val priceEditText: EditText = dialogView.findViewById(R.id.edit_price)

        nameEditText.setText(product.name)
        manufacturerEditText.setText(product.manufacturer)
        yearEditText.setText(product.year.toString())
        quantityEditText.setText(product.quantity.toString())
        priceEditText.setText(product.price.toString())

        AlertDialog.Builder(this)
            .setTitle("Редактировать продукт")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { dialog, _ ->
                product.name = nameEditText.text.toString()
                product.manufacturer = manufacturerEditText.text.toString()
                product.year = yearEditText.text.toString().toInt()
                product.quantity = quantityEditText.text.toString().toInt()
                product.price = priceEditText.text.toString().toDouble()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        productDao.updateProduct(product)
                    }
                    loadProducts()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Удалить") { dialog, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        productDao.deleteProduct(product)
                    }
                    loadProducts()
                }
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun calculateTotalPriceForCurrentYear() {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        lifecycleScope.launch {
            val total = withContext(Dispatchers.IO) {
                productDao.getAllProducts().filter { it.year == currentYear }.sumOf { it.price * it.quantity }
            }
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Общая стоимость")
                .setMessage("Общая стоимость товаров, выпущенных в $currentYear году: $total")
                .setPositiveButton("ОК") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
}
