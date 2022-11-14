package com.almazov.diacompanion.data

import androidx.room.ColumnInfo
import androidx.room.Entity


class DateClass {
    @ColumnInfo(name = "date")
    var date: String? = null
    var fullDay: Boolean? = false
}