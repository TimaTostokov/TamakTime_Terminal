package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItemFull
import com.pos_terminal.tamaktime_temirnal.databinding.ItemOrderBinding

class OrderItemAdapter(private val listener: OrderItemListener) :
    RecyclerView.Adapter<OrderItemViewHolder>() {

    interface OrderItemListener {
        fun onAdd(orderItemFull: OrderItemFull)
        fun onRemove(orderItemFull: OrderItemFull)
        fun onDelete(orderItemFull: OrderItemFull)
    }

    private var items = mutableListOf<OrderItemFull>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<OrderItemFull>) {
        Log.d("OrderItmAdapter", "items: $items")
        items.apply {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
        Log.d("OrderItemAdapter", "Items set: ${items.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}

class OrderItemViewHolder(
    val binding: ItemOrderBinding,
    private val listener: OrderItemAdapter.OrderItemListener,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private var orderItemFull: OrderItemFull? = null

    init {
        with(binding) {
            ibDelete.setOnClickListener(this@OrderItemViewHolder)
            ibRemove.setOnClickListener(this@OrderItemViewHolder)
            ibAdd.setOnClickListener(this@OrderItemViewHolder)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(orderItem: OrderItemFull) {
        this.orderItemFull = orderItem
        Log.d("OrderItemViewHolder", "Binding item: ${orderItem.product?.title}")
        orderItem.product?.thumbnail = orderItem.product?.thumbnail.takeIf { !it.isNullOrEmpty() }
            ?: "https://picsum.photos/66/66?random=${orderItem.product?.id}"

        Glide.with(binding.image.context).load(orderItem.product?.thumbnail).into(binding.image)
        binding.total.text = ((orderItemFull!!.product?.sellingPrice?.toDouble()
            ?: 0.0) * orderItemFull!!.count!!).toString()
        binding.title.text = orderItemFull!!.product?.title
        binding.count.text = orderItemFull!!.count.toString()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ibAdd -> {
                binding.count.text = (orderItemFull?.count?.plus(1)).toString()
                updatePrice('+')
                orderItemFull?.let { listener.onAdd(it) }
            }

            binding.ibRemove -> {
                if (orderItemFull?.count!! > 1) {
                    binding.count.text = (orderItemFull!!.count.minus(1)).toString()
                    updatePrice('-')
                    listener.onRemove(orderItemFull!!)
                }
            }

            binding.ibDelete -> orderItemFull?.let { listener.onDelete(it) }
        }
    }

    private fun updatePrice(operator: Char) {
        when (operator) {
            '+' -> {
                binding.total.text = (binding.total.text.toString()
                    .toDouble() + orderItemFull?.product?.sellingPrice!!.toDouble()).toString()
            }

            '-' -> {
                binding.total.text = (binding.total.text.toString()
                    .toDouble() - orderItemFull?.product?.sellingPrice!!.toDouble()).toString()
            }
        }
    }

}