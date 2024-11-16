package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItemFull
import com.pos_terminal.tamaktime_temirnal.databinding.ItemOrderBinding

class OrderItemAdapter(
    private val listener: OrderItemListener
) : ListAdapter<OrderItemFull, OrderItemAdapter.OrderItemViewHolder>(DiffCallback()) {

    interface OrderItemListener {
        fun onAdd(orderItemFull: OrderItemFull)
        fun onRemove(orderItemFull: OrderItemFull)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderItemViewHolder(
        private val binding: ItemOrderBinding,
        private val listener: OrderItemListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var orderItemFull: OrderItemFull? = null

        init {
            binding.ibAdd.setOnClickListener {
                orderItemFull?.let {
                    if (it.product?.count ?: 0 > 0) {
                        listener.onAdd(it)
                    }
                }
            }
            binding.ibRemove.setOnClickListener {
                orderItemFull?.let { listener.onRemove(it) }
            }
        }

        fun bind(orderItem: OrderItemFull) {
            orderItemFull = orderItem
            binding.title.text = orderItem.product?.title
            binding.count.text = orderItem.count.toString()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<OrderItemFull>() {
        override fun areItemsTheSame(oldItem: OrderItemFull, newItem: OrderItemFull): Boolean =
            oldItem.product?.id == newItem.product?.id

        override fun areContentsTheSame(oldItem: OrderItemFull, newItem: OrderItemFull): Boolean =
            oldItem == newItem
    }
}


