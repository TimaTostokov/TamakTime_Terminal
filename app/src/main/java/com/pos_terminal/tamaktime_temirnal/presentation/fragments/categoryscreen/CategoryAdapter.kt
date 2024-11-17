package com.pos_terminal.tamaktime_temirnal.presentation.fragments.categoryscreen

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pos_terminal.tamaktime_temirnal.common.loadImageURL
import com.pos_terminal.tamaktime_temirnal.data.remote.model.category.Category
import com.pos_terminal.tamaktime_temirnal.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val click: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.onBind(category)
        holder.itemView.setOnClickListener {
            click(category)
        }
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(category: Category) {
            binding.catTitle.text = category.name
            binding.catIcon.loadImageURL(category.icon)
            binding.llCat.setBackgroundColor(Color.parseColor(category.color))
        }
    }
}

class CategoryDiffUtil : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }

}