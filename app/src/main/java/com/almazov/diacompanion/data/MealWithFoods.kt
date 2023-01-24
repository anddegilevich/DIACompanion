package com.almazov.diacompanion.data

import androidx.room.*


data class MealWithFoods (
    @Embedded
    val mealEntity: MealEntity,
    @Relation(
        entity = FoodInMealEntity::class,
        parentColumn = "idMeal",
        entityColumn = "idMeal"
    )
    val foods: List<FoodsWithWeight>
)