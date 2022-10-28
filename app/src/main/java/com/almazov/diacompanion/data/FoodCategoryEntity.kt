package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "food_category_table")
data class FoodCategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val category: String?
): Parcelable