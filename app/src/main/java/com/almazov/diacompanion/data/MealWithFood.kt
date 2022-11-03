package com.almazov.diacompanion.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MealWithFood (

    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "idMeal",
        entityColumn = "idFood",
        associateBy = Junction(FoodInMealEntity::class)
    )
    val foods: List<FoodEntity>
)