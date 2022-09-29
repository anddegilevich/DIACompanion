package com.almazov.diacompanion.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.databinding.CardCategoryBinding

class CategoriesAdapter(private val categories: List<Category>,
                        private var categoryClickListener: CategoryClickListener)
    : RecyclerView.Adapter<CardCategoryViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardCategoryViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCategoryBinding.inflate(from, parent, false)
        return CardCategoryViewHolder(binding, categoryClickListener)
    }

    override fun onBindViewHolder(holder: CardCategoryViewHolder, position: Int) {

        holder.bindCategory(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}