package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "sugar_level_table")
data class SugarLevelEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val sugarLevel: Double?,
    val preferences: String?,
    val wasPhysicalAct: Boolean?
): Parcelable