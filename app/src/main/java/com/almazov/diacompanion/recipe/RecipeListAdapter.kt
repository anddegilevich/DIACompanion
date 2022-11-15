package com.almazov.diacompanion.recipe

import android.view.View
import androidx.navigation.findNavController
import com.almazov.diacompanion.data.FoodEntity
import com.almazov.diacompanion.meal.FoodListAdapter
import kotlinx.android.synthetic.main.food_row.view.*

class RecipeListAdapter(): FoodListAdapter() {

    override fun setItemViewClickListener(holder: FoodViewHolder, food: FoodEntity?) {
        holder.itemView.setOnClickListener{ view ->
            val action = RecipeListDirections.actionRecipeListToAddRecipe()
            view.findNavController().navigate(action)
        }

        holder.itemView.checkbox_favourite.visibility = View.GONE

        holder.itemView.image_recipe.visibility = View.VISIBLE
    }
}