package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "food_table")
data class FoodEntity (
    @PrimaryKey(autoGenerate = true)
    var idFood: Int?,
    val category: String?,
    val name: String?
): Parcelable