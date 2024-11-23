package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen

import android.annotation.SuppressLint
import android.util.Log
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
import com.pos_terminal.tamaktime_temirnal.databinding.ItemProductBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class ProductAdapter(
    private val listener: OnProductClickListener,
    private val isUserAuthenticatedFlow: StateFlow<Boolean>
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private var isUserAuthenticated = false

    override fun submitList(list: List<Product>?) {
        super.submitList(list?.map { it.copy() })
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            isUserAuthenticatedFlow.collect { authenticated ->
                isUserAuthenticated = authenticated
                notifyDataSetChanged()
            }
        }
    }

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: Product?) {

            if (product == null) return
            product.thumbnail?.let {
                binding.foodImage.loadImageApi(it)
                Log.w("ProductViewHolder", "Thumbnail URL is null${it}")
            } ?: run {
                binding.foodImage.setImageResource(R.drawable.img_food)
            }
            binding.foodImage.loadImageApi(product.thumbnail.toString())
            binding.foodName.text = product.title
            binding.foodCount.text = "${product.count} шт"
            binding.foodPrice.text = formatPrice(product.sellingPrice?.toDoubleOrNull() ?: 0.0)

            val isClickable = product.count > 0 && isUserAuthenticated
            binding.root.isEnabled = isClickable
            binding.root.alpha = if (isClickable) 1.0f else 0.5f

            binding.root.setOnClickListener {
                if (isClickable) {
                    listener.onProductClick(product)
                } else {
                    Toast.makeText(
                        itemView.context,
                        "Пожалуйста, пройдите аутентификацию",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val textColor = if (product.count > 0) android.R.color.black else R.color.gray
            binding.foodPrice.setTextColor(itemView.context.getColor(textColor))
            binding.foodCount.setTextColor(itemView.context.getColor(textColor))
            binding.foodName.setTextColor(itemView.context.getColor(textColor))
        }
    }
}


class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}