package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.almazov.diacompanion.data.RecordEntity

@Dao
interface AppDao {

    //Records
    @Query("SELECT id FROM record_table ORDER BY id DESC LIMIT 1")
    fun getLastRecordId(): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(recordEntity: RecordEntity): Long

    @Update
    suspend fun updateRecord(recordEntity: RecordEntity)

    @Delete
    suspend fun deleteRecord(recordEntity: RecordEntity)

    @Query("DELETE FROM record_table")
    suspend fun deleteAllRecords()

    @Query("SELECT * FROM record_table ORDER BY id")
    fun readAllData(): LiveData<List<RecordEntity>>

    @Query("SELECT * FROM record_table WHERE category LIKE :filter")
    fun filterDatabase(filter: String): LiveData<List<RecordEntity>>

    // SugarLevel
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(sugarLevelEntity: SugarLevelEntity)

    @Update
    suspend fun updateRecord(sugarLevelEntity: SugarLevelEntity)

    @Query("DELETE FROM sugar_level_table WHERE id LIKE :id")
    suspend fun deleteSugarLevelRecord(id: Int?)

    @Query("SELECT * FROM sugar_level_table WHERE id LIKE :id LIMIT 1")
    fun readSugarLevelRecord(id: Int?): LiveData<SugarLevelEntity>

    // Insulin
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(insulinEntity: InsulinEntity)


}