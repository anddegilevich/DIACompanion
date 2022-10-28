package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "ketone_table")
data class KetoneEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val ketone: Int?,
    ): Parcelable