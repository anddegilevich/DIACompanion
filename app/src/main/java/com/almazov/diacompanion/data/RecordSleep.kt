package com.almazov.diacompanion.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecordSleep (
    @Embedded val recordEntity: RecordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val sleepEntity: SleepEntity
)