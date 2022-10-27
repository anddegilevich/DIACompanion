package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Callable

class DatabaseRepository(private val appDao: AppDao) {

    val readAllData: LiveData<List<RecordEntity>> = appDao.readAllData()

    //Records
    suspend fun addRecord(recordEntity: RecordEntity){
        appDao.addRecord(recordEntity)
    }

    fun addRecord(sugarLevelEntity: SugarLevelEntity){
        appDao.addRecord(sugarLevelEntity)
    }

    suspend fun updateRecord(recordEntity: RecordEntity){
        appDao.updateRecord(recordEntity)
    }

    suspend fun deleteRecord(recordEntity: RecordEntity){
        appDao.deleteRecord(recordEntity)
    }

    suspend fun deleteAllRecords(){
        appDao.deleteAllRecords()
    }

    fun filterDatabase(filter: String): LiveData<List<RecordEntity>> {
        return appDao.filterDatabase(filter)
    }

    //SugarLevel
    /*suspend fun addSugarLevel(sugarLevelEntity: SugarLevelEntity) {
        appDao.addSugarLevel(sugarLevelEntity)
    }

    suspend fun updateSugarLevel(sugarLevelEntity: SugarLevelEntity){
        appDao.updateSugarLevel(sugarLevelEntity)
    }

    suspend fun deleteSugarLevel(sugarLevelEntity: SugarLevelEntity){
        appDao.deleteSugarLevel(sugarLevelEntity)
    }

    suspend fun readSugarLevel(id: String): SugarLevelEntity {
        return appDao.readSugarLevel(id)
    }*/

}