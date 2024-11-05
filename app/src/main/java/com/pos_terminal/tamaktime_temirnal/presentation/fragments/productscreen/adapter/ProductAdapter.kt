package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.databinding.ItemProductBinding

class ProductAdapter(private val listener: ProductItemListener) :
    RecyclerView.Adapter<ProductItemViewHolder>() {

    interface ProductItemListener {
        fun onClickedProduct(product: Product)
    }

    private val items = ArrayList<Product>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<Product>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val binding: ItemProductBinding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ProductItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

class ProductItemViewHolder(
    private val itemBinding: ItemProductBinding,
    private val listener: ProductAdapter.ProductItemListener,
) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
    private lateinit var product: Product
    private val disabledColor = Color.parseColor(buildString {
        append("#9C9C9C")
    })

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(product: Product) {

        this.product = product
        product.thumbnail?.let {
            Glide.with(itemBinding.foodImage.context)
                .load(product.thumbnail)
                .into(itemBinding.foodImage)
        }
        itemBinding.foodName.text = product.title
        itemBinding.foodPrice.text = buildString {
            append(product.sellingPrice?.toDouble()?.toInt())
            append(" ₺")
        }
        itemBinding.foodCount.text = buildString {
            append(product.count)
            append(" шт")
        }
        itemBinding.foodCount.setTextColor(Color.parseColor(buildString {
            append("#878787")
        }))
        if (!product.available || product.count!! <= 0) {
            itemBinding.foodName.setTextColor(disabledColor)
        }
    }

    override fun onClick(v: View?) {
        if (product.available) {
            listener.onClickedProduct(product)
        }
    }

}