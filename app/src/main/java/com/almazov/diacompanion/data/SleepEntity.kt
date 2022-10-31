package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_table")
data class SleepEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    val duration: Double?
    )