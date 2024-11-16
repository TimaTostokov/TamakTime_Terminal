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

class OrderItemAdapter(private val listener: OrderItemListener) :
    ListAdapter<OrderItemFull, OrderItemAdapter.OrderItemViewHolder>(DiffCallback()) {

    interface OrderItemListener {
        fun onAdd(orderItemFull: OrderItemFull)
        fun onRemove(orderItemFull: OrderItemFull)
        fun onDelete(orderItemFull: OrderItemFull)
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
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private var orderItemFull: OrderItemFull? = null

        init {
            binding.ibAdd.setOnClickListener(this)
            binding.ibRemove.setOnClickListener(this)
            binding.ibDelete.setOnClickListener(this)
        }

        fun bind(orderItem: OrderItemFull) {
            orderItemFull = orderItem
            Glide.with(binding.image.context)
                .load(orderItem.product?.thumbnail ?: "https://example.com/default.png")
                .into(binding.image)
            binding.title.text = orderItem.product?.title
            binding.count.text = orderItem.count.toString()
            binding.total.text = ((orderItem.product?.sellingPrice?.toDouble() ?: 0.0) * orderItem.count).toString()
        }

        override fun onClick(view: View?) {
            when (view) {
                binding.ibAdd -> orderItemFull?.let {
                    listener.onAdd(it)
                }
                binding.ibRemove -> orderItemFull?.let {
                    listener.onRemove(it)
                }
                binding.ibDelete -> orderItemFull?.let {
                    listener.onDelete(it)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<OrderItemFull>() {
        override fun areItemsTheSame(oldItem: OrderItemFull, newItem: OrderItemFull): Boolean =
            oldItem.product?.id == newItem.product?.id

        override fun areContentsTheSame(oldItem: OrderItemFull, newItem: OrderItemFull): Boolean =
            oldItem == newItem
    }
}

