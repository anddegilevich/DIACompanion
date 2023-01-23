package com.almazov.diacompanion.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation


data class RecordWithMealWithFoods (
    @Embedded val recordEntity: RecordEntity,
    @Relation(
        entity = MealEntity::class,
        parentColumn = "id",
        entityColumn = "idMeal"
    )
    val mealWithFoods: MealWithFoods
)