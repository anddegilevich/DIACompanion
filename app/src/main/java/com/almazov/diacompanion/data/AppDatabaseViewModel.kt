package com.almazov.diacompanion.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDatabaseViewModel(application: Application): AndroidViewModel(application) {

    val readALlData: LiveData<List<RecordEntity>>
    val readAllPaged = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            maxSize = 200
        )
    ){
        repository.readAllPaged
    }.flow

    private val repository: AppDatabaseRepository

    init {
        val appDao = AppDatabase.getDatabase(application).appDao()
        repository = AppDatabaseRepository(appDao)
        readALlData = repository.readAllData
    }

    // Records

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

    // SugarLevel

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

    // Insulin
    fun addRecord(recordEntity: RecordEntity, insulinEntity: InsulinEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            insulinEntity.id = id.toInt()
            repository.addRecord(insulinEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity, insulinEntity: InsulinEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(insulinEntity)
        }
    }

    fun readInsulinRecord(id: Int?): LiveData<InsulinEntity>{
        return repository.readInsulinRecord(id)
    }

    fun deleteInsulinRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteInsulinRecord(id)
        }
    }

    // Meal
    fun addRecord(recordEntity: RecordEntity, mealEntity: MealEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            mealEntity.id = id.toInt()
            repository.addRecord(mealEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity, mealEntity: MealEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(mealEntity)
        }
    }

    fun readMealRecord(id: Int?): LiveData<MealEntity>{
        return repository.readMealRecord(id)
    }

    fun deleteMealRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMealRecord(id)
        }
    }

    // Workout
    fun addRecord(recordEntity: RecordEntity, workoutEntity: WorkoutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            workoutEntity.id = id.toInt()
            repository.addRecord(workoutEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity, workoutEntity: WorkoutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(workoutEntity)
        }
    }

    fun readWorkoutRecord(id: Int?): LiveData<WorkoutEntity>{
        return repository.readWorkoutRecord(id)
    }

    fun deleteWorkoutRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkoutRecord(id)
        }
    }

    // Sleep
    fun addRecord(recordEntity: RecordEntity, sleepEntity: SleepEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            sleepEntity.id = id.toInt()
            repository.addRecord(sleepEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity, sleepEntity: SleepEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(sleepEntity)
        }
    }

    fun readSleepRecord(id: Int?): LiveData<SleepEntity>{
        return repository.readSleepRecord(id)
    }

    fun deleteSleepRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSleepRecord(id)
        }
    }

    // Weight
    fun addRecord(recordEntity: RecordEntity, weightEntity: WeightEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            weightEntity.id = id.toInt()
            repository.addRecord(weightEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity, weightEntity: WeightEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(weightEntity)
        }
    }

    fun readWeightRecord(id: Int?): LiveData<WeightEntity>{
        return repository.readWeightRecord(id)
    }

    fun deleteWeightRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWeightRecord(id)
        }
    }

    // Ketone
    fun addRecord(recordEntity: RecordEntity, ketoneEntity: KetoneEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            ketoneEntity.id = id.toInt()
            repository.addRecord(ketoneEntity)
        }
    }

    fun updateRecord(recordEntity: RecordEntity, ketoneEntity: KetoneEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(ketoneEntity)
        }
    }

    fun readKetoneRecord(id: Int?): LiveData<KetoneEntity>{
        return repository.readKetoneRecord(id)
    }

    fun deleteKetoneRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteKetoneRecord(id)
        }
    }
}