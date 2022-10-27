package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    //Records
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(recordEntity: RecordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecord(sugarLevelEntity: SugarLevelEntity)

    @Update
    suspend fun updateRecord(recordEntity: RecordEntity)

    @Delete
    suspend fun deleteRecord(recordEntity: RecordEntity)

    @Query("DELETE FROM record_table")
    suspend fun deleteAllRecords()

    @Query("SELECT * FROM record_table ORDER BY id")
    fun readAllData(): LiveData<List<RecordEntity>>

    /*@Query("SELECT * FROM record_table JOIN sugar_level_table ON record_table.idCategory = sugar_level_table.id")
    fun getFullSugarLevelInfo(): Map<RecordEntity,SugarLevelEntity>*/

    @Query("SELECT * FROM record_table WHERE category LIKE :filter")
    fun filterDatabase(filter: String): LiveData<List<RecordEntity>>

    // SugarLevel
    /*@Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSugarLevel(sugarLevelEntity: SugarLevelEntity): Long

    @Update
    suspend fun updateSugarLevel(sugarLevelEntity: SugarLevelEntity)

    @Delete
    suspend fun deleteSugarLevel(sugarLevelEntity: SugarLevelEntity)

    @Query("SELECT * FROM sugar_level_table WHERE id LIKE :id")
    suspend fun readSugarLevel(id: String): SugarLevelEntity*/
}