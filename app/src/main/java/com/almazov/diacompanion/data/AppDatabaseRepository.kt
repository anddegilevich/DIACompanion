package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData

class AppDatabaseRepository(private val appDao: AppDao) {

    val readAllData: LiveData<List<RecordEntity>> = appDao.readAllData()

    //Records
    suspend fun addRecord(recordEntity: RecordEntity): Long {
        return appDao.addRecord(recordEntity)
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
    suspend fun addRecord(sugarLevelEntity: SugarLevelEntity){
        appDao.addRecord(sugarLevelEntity)
    }

    fun readSugarLevelRecord(id: Int?): LiveData<SugarLevelEntity>{
        return appDao.readSugarLevelRecord(id)
    }

    suspend fun updateRecord(sugarLevelEntity: SugarLevelEntity){
        appDao.updateRecord(sugarLevelEntity)
    }
    suspend fun deleteSugarLevelRecord(id: Int?){
        appDao.deleteSugarLevelRecord(id)
    }

    //Insulin
    suspend fun addRecord(insulinEntity: InsulinEntity){
        appDao.addRecord(insulinEntity)
    }

}