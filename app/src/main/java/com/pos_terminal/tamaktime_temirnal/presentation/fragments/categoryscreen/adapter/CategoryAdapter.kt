package com.pos_terminal.tamaktime_temirnal.presentation.fragments.categoryscreen.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.data.remote.model.category.Category
import com.pos_terminal.tamaktime_temirnal.databinding.ItemCategoryBinding

class CategoryAdapter(private val listener: CategoryItemListener) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    interface CategoryItemListener {
        fun onClickedCategory(category: Category)
    }

    private val items: MutableList<Category> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Category>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding: ItemCategoryBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

class CategoryViewHolder(
    private val itemBinding: ItemCategoryBinding,
    private val listener: CategoryAdapter.CategoryItemListener,
) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

    private lateinit var category: Category

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(category: Category) {
        this.category = category
        category.icon?.let { iconUrl ->
            Glide.with(itemBinding.catIcon.context)
                .load(iconUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(itemBinding.catIcon)
        }
        itemBinding.catTitle.text = category.name
        itemBinding.catCard.setCardBackgroundColor(
            category.color?.let { Color.parseColor(it) } ?: Color.WHITE
        )
    }

    override fun onClick(v: View?) {
        listener.onClickedCategory(category)
    }

}