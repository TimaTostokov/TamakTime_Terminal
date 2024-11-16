package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.databinding.ItemProductBinding
class ProductAdapter(
    private val products: MutableList<Product>,
    private val listener: OnProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            if (product.count!! > 0) {
                listener.onProductClick(product)
            }
        }

        holder.itemView.isEnabled = product.count!! > 0
        holder.binding.foodPrice.setTextColor(
            if (product.count!! > 0) holder.itemView.context.getColor(android.R.color.black)
            else holder.itemView.context.getColor(R.color.gray)
        )
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.foodName.text = product.title
            binding.foodCount.text = product.count.toString()
            binding.foodPrice.text = product.sellingPrice.toString()

            if (product.count!! > 0) {
                binding.foodPrice.setTextColor(binding.root.context.getColor(android.R.color.black))
            } else {
                binding.foodPrice.setTextColor(binding.root.context.getColor(R.color.gray))
            }
        }
    }
}



