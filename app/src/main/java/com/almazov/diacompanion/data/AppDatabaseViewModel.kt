package com.almazov.diacompanion.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDatabaseViewModel(application: Application): AndroidViewModel(application) {

    val readALlData: LiveData<List<RecordEntity>>
    private val repository: DatabaseRepository

    init {
        val appDao = AppDatabase.getDatabase(application).appDao()
        repository = DatabaseRepository(appDao)
        readALlData = repository.readAllData
    }

    fun addRecord(recordEntity: RecordEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.addRecord(recordEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateRecord(recordEntity)
        }
    }

    fun deleteRecord(recordEntity: RecordEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteRecord(recordEntity)
        }
    }

    fun deleteAllRecords(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllRecords()
        }
    }
}