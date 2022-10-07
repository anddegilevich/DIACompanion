package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat

@Entity(tableName = "sleep_table")
data class SleepEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val duration: DecimalFormat
    )