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

    fun addRecord(recordEntity: RecordEntity, sugarLevelEntity: SugarLevelEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            sugarLevelEntity.id = id.toInt()
            repository.addRecord(sugarLevelEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity, sugarLevelEntity: SugarLevelEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(sugarLevelEntity)
        }
    }

    fun readSugarLevelRecord(id: Int?): LiveData<SugarLevelEntity>{
        return repository.readSugarLevelRecord(id)
    }

    fun deleteSugarLevelRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSugarLevelRecord(id)
        }
    }

    //Insulin
    fun addRecord(recordEntity: RecordEntity, insulinEntity: InsulinEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            insulinEntity.id = id.toInt()
            repository.addRecord(insulinEntity)
        }
    }
}