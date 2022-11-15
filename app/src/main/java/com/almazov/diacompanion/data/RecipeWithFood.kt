package com.almazov.diacompanion.data

import androidx.room.Embedded

data class RecipeWithFood (

    @Embedded val recipe: FoodEntity,
    @Embedded val food: FoodEntity,
    val weight: Double?
)