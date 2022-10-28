package com.almazov.diacompanion.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDatabaseViewModel(application: Application): AndroidViewModel(application) {

    val readALlData: LiveData<List<RecordEntity>>
    private val repository: AppDatabaseRepository

    init {
        val appDao = AppDatabase.getDatabase(application).appDao()
        repository = AppDatabaseRepository(appDao)
        readALlData = repository.readAllData
    }

    //Records

    fun addRecord(recordEntity: RecordEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecord(recordEntity)
        }
    }


    fun addRecord(sugarLevelEntity: SugarLevelEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecord(sugarLevelEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
        }
    }

    fun deleteRecord(recordEntity: RecordEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecord(recordEntity)
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllRecords()
        }
    }

    fun filterDatabase(filter: String): LiveData<List<RecordEntity>> {
        return repository.filterDatabase(filter)
    }

    //SugarLevel
    /* fun addSugarLevel(sugarLevelEntity: SugarLevelEntity) {
            

        }*/

}