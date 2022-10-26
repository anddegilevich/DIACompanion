package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(recordEntity: RecordEntity)

    @Update
    suspend fun updateRecord(recordEntity: RecordEntity)

    @Delete
    suspend fun deleteRecord(recordEntity: RecordEntity)

    @Query("DELETE FROM record_table")
    suspend fun deleteAllRecords()

    @Query("SELECT * FROM record_table ORDER BY id")
    fun readAllData(): LiveData<List<RecordEntity>>
}