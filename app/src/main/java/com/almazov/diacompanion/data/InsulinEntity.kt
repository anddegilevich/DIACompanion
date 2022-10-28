package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "insulin_table")
data class InsulinEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val insulin: Int?,
    val type: String?,
    val preferences: String?,
    ): Parcelable