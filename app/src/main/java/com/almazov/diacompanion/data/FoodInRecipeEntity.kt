package com.almazov.diacompanion.data

import androidx.room.Entity

@Entity(tableName = "food_in_recipe_table", primaryKeys = ["idRecipe", "idFood"])
data class FoodInRecipeEntity(
    val idRecipe: Int,
    val idFood: Int,
    val weight: Double?
)