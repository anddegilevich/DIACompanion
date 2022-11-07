package com.almazov.diacompanion.data

import androidx.room.Entity

@Entity(tableName = "food_in_meal_table", primaryKeys = ["idMeal", "idFood"])
data class FoodInMealEntity (
    val idMeal: Int,
    val idFood: Int,
    val weight: Double?
)