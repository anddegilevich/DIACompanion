package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat

@Entity(tableName = "weight_table")
data class WeightEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val weight: DecimalFormat
    )