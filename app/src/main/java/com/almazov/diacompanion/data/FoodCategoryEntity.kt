package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_category_table")
data class FoodCategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val category: String
)