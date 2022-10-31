package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sugar_level_table")
data class SugarLevelEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    val sugarLevel: Double?,
    val preferences: String?,
    val wasPhysicalAct: Boolean?
)