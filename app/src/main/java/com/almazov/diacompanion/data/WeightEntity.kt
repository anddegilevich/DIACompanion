package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weight_table")
data class WeightEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    val weight: Double?
    )