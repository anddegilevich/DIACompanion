package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData

class DatabaseRepository(private val appDao: AppDao) {

    val readAllData: LiveData<List<RecordEntity>> = appDao.readAllData()

    suspend fun addRecord(recordEntity: RecordEntity){
        appDao.addRecord(recordEntity)
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

}