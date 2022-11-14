package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "record_table")
data class RecordEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val category: String?,
    val mainInfo: String?,
    val dateInMilli: Long?,
    val time: String?,
    val date: String?,
    val dateSubmit: Long?,
    var fullDay: Boolean?
): Parcelable