package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.Extensions.formatPrice
import com.pos_terminal.tamaktime_temirnal.common.loadImageApi
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.databinding.ItemOrderBinding

class OrderItemAdapter(
    private val listener: OrderItemListener
) : ListAdapter<Product, OrderItemAdapter.OrderItemViewHolder>(OrderDiffCallback()) {

    interface OrderItemListener {
        fun onAdd(product: Product)
        fun onRemove(product: Product)
        fun onDelete(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderItemViewHolder(
        private val binding: ItemOrderBinding,
        private val listener: OrderItemListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var product: Product? = null

        init {
            binding.ibAdd.setOnClickListener {
                product?.let {
                    if (it.count > 0) {
                        listener.onAdd(it)
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            binding.root.context.getString(R.string.product_out_of_stock),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            binding.ibRemove.setOnClickListener {
                product?.let { listener.onRemove(it) }
            }
            binding.ibDelete.setOnClickListener {
                product?.let { listener.onDelete(it) }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            this.product = product
            binding.title.text = product.title
            binding.count.text = product.cartCount.toString()
            binding.image.loadImageApi(product.thumbnail.toString())
            val total = (product.sellingPrice?.toDoubleOrNull() ?: 0.0) * product.cartCount
            binding.total.text = formatPrice(total)

            binding.ibAdd.isEnabled = product.count > 0
        }
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}