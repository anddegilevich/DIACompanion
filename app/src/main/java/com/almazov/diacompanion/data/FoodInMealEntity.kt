package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat

@Entity(tableName = "food_in_meal_table")
data class FoodInMealEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val idFood: Int,
    val weight: DecimalFormat
)