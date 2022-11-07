package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_table")
data class MealEntity (
    @PrimaryKey(autoGenerate = true)
    var idMeal: Int?,
    val type: String?
    )