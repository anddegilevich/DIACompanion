package com.almazov.diacompanion.data

import androidx.room.Embedded
import androidx.room.Relation

data class FoodsWithWeight (
    @Embedded
    val foodInMealEntity: FoodInMealEntity,
    @Relation(
        parentColumn = "idFood",
        entityColumn = "idFood",
    )
    val food: FoodEntity
)