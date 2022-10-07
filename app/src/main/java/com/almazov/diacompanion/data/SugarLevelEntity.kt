package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat

@Entity(tableName = "sugar_level_table")
data class SugarLevelEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sugarLevel: DecimalFormat,
    val preferences: String,
    val wasPhysicalAct: Boolean
)