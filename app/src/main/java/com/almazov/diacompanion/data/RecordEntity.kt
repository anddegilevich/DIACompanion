package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_table")
data class RecordEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val category: String,
    val idCategory: Int,
    val mainInfo: String,
    val time: String,
    val date: String,
    val dateSubmit: String,
    val fullDay: Boolean
)