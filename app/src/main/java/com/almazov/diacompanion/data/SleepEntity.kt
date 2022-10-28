package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "sleep_table")
data class SleepEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val duration: Double?
    ): Parcelable