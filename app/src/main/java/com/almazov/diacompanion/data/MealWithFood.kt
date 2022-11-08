package com.almazov.diacompanion.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MealWithFood (

    @Embedded val meal: MealEntity,
    @Embedded val food: FoodEntity,
    val weight: Double?
)