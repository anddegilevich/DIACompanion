package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_table")
data class WorkoutEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val duration: Int,
    val type: Int
    )