package com.almazov.diacompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat

@Entity(tableName = "ketone_table")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val username: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val patronymic: String,
    val birthDate: String,
    val gender: String,
    val email: String,
    val phone: String,
    val height: DecimalFormat,
    val attendingDoctor: String,
    val pregnancyStart: String,
)