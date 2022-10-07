package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_table")
data class RecipeEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val gi: Int,
    //todo add
)