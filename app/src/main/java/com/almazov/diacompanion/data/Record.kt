package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.almazov.diacompanion.categories.Category

@Entity(tableName = "record_table")
data class Record (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val category: Category,
    val time: String,
    val date: String
)