package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class FoodEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val gi: Int,
    //todo add
)