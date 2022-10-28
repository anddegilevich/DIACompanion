package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "weight_table")
data class WeightEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val weight: Double?
    ): Parcelable