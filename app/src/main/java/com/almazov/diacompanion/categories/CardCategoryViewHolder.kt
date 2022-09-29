package com.almazov.diacompanion.categories

import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.databinding.CardCategoryBinding

class CardCategoryViewHolder(
    private val cardCategoryBinding: CardCategoryBinding,
    private var categoryClickListener: CategoryClickListener
): RecyclerView.ViewHolder(cardCategoryBinding.root)
{
    fun bindCategory(category: Category)
    {
        cardCategoryBinding.CategoryIcon.setImageResource(category.icon)
        cardCategoryBinding.CategoryName.setText(category.name)
        cardCategoryBinding.CategoryName.setTextColor(category.secondary_color)
        cardCategoryBinding.Content.setBackgroundColor(category.primary_color)

        cardCategoryBinding.CardView.setOnClickListener{
            categoryClickListener.onClick(category)
        }
    }
}   