package com.almazov.diacompanion.data

import androidx.room.Embedded

data class MealFullInfo (

    @Embedded(prefix = "record_") val record: RecordEntity,
    @Embedded val meal: MealEntity,
    @Embedded(prefix = "food_") val food: FoodEntity,
    val weight: Double?
)