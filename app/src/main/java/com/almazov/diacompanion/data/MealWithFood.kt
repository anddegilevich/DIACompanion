package com.almazov.diacompanion.data

import androidx.room.Embedded

data class MealWithFood (

    @Embedded val meal: MealEntity,
    @Embedded val food: FoodEntity,
    val weight: Double?
)