package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat

@Entity(tableName = "full_day_table")
data class FullDaysEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val dateSubmit: String
)