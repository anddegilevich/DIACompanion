package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "meal_table")
data class MealEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val kkalPlus: Double?
    ): Parcelable