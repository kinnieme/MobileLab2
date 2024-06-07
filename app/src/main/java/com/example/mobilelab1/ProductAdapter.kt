package com.example.mobilelab1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<ProductEntity>,
    private val onItemClick: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.product_name)
        private val manufacturerTextView: TextView = itemView.findViewById(R.id.product_manufacturer)
        private val yearTextView: TextView = itemView.findViewById(R.id.product_year)
        private val quantityTextView: TextView = itemView.findViewById(R.id.product_quantity)
        private val priceTextView: TextView = itemView.findViewById(R.id.product_price)

        fun bind(product: ProductEntity) {
            nameTextView.text = product.name
            manufacturerTextView.text = product.manufacturer
            yearTextView.text = product.year.toString()
            quantityTextView.text = product.quantity.toString()
            priceTextView.text = product.price.toString()

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
}
