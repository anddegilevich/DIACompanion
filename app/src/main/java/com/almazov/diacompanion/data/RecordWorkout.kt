package com.almazov.diacompanion.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecordWorkout (
    @Embedded val recordEntity: RecordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val workoutEntity: WorkoutEntity
)