package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(record: Record)

    @Query("SELECT * FROM record_table ORDER BY id")
    fun readAllData(): LiveData<List<Record>>
}