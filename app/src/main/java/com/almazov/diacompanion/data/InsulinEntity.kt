package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin_table")
data class InsulinEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val insulin: Int,
    val type: String,
    val preferences: String,
    )