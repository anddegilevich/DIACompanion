package com.almazov.diacompanion.data

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.almazov.diacompanion.meal.FoodInMealItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AppDatabaseViewModel(application: Application): AndroidViewModel(application) {



    fun readDatesPaged(): Flow<PagingData<DateClass>> {
        return Pager(
            PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,
                maxSize = 60
            )
        ){
            repository.readDatesPaged()
        }.flow
    }

    private val repository: AppDatabaseRepository

    init {
        val appDao = AppDatabase.getDatabase(application).appDao()
        repository = AppDatabaseRepository(appDao)
    }

    // Records

    fun readLastRecords(): LiveData<List<RecordEntity>> {
        return repository.readLastRecords()
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

    fun readDayRecords(date: String?): LiveData<List<RecordEntity>> {
        return repository.readDayRecords(date)
    }

    fun readDayRecords(date: String?, filter: String): LiveData<List<RecordEntity>> {
        return repository.readDayRecords(date, filter)
    }

    fun updateFullDays(date: String?, fullDay: Boolean?) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateFullDays(date,fullDay)
        }
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

    fun addRecord(recordEntity: RecordEntity, mealEntity: MealEntity, foodList: MutableList<FoodInMealItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            mealEntity.idMeal = id.toInt()
            repository.addRecord(mealEntity)
            for (food in foodList) {
                val foodInMealEntity = FoodInMealEntity(id.toInt(), food.foodEntity.idFood!!,food.weight)
                repository.addRecord(foodInMealEntity)
            }
        }
    }

    fun updateRecord(recordEntity: RecordEntity, mealEntity: MealEntity, foodList: MutableList<FoodInMealItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(mealEntity)
            repository.deleteMealWithFoodsRecord(recordEntity.id)
            for (food in foodList) {
                val foodInMealEntity = FoodInMealEntity(recordEntity.id!!, food.foodEntity.idFood!!,food.weight)
                repository.addRecord(foodInMealEntity)
            }
        }
    }

    fun deleteMealRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMealRecord(id)
            repository.deleteMealWithFoodsRecord(id)
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

    // Food

    fun readFoodPaged(): Flow<PagingData<FoodEntity>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            repository.readFoodPaged
        }.flow
    }

    fun readFoodPagedFilter(filter: String): Flow<PagingData<FoodEntity>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            repository.readFoodPagedFilter(filter)
        }.liveData.map {
            val foodMap = mutableSetOf<Int?>()
            it.filter { food ->
                if (foodMap.contains(food.idFood)) {
                    false
                } else {
                    foodMap.add(food.idFood)
                }
            }
        }.asFlow()
    }

    fun updateFavourite(id: Int?, favourite: Boolean?) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateFavourite(id,favourite)
        }
    }

    // Food in meal

    fun getMealWithFoods(id: Int?): LiveData<List<MealWithFood>>{
        return repository.getMealWithFoods(id)
    }

    // Recipe

    fun addRecord(foodEntity: FoodEntity, foodList: MutableList<FoodInMealItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(foodEntity)
            for (food in foodList) {
                val foodInRecipeEntity = FoodInRecipeEntity(id.toInt(), food.foodEntity.idFood!!,food.weight)
                repository.addRecord(foodInRecipeEntity)
            }
        }
    }

    fun updateRecord(foodEntity: FoodEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(foodEntity)
        }
    }

    fun deleteRecipeRecord(id: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecipeRecord(id)
        }
    }

    fun readRecipePaged(): Flow<PagingData<FoodEntity>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            repository.readRecipePaged()
        }.flow
    }

    //Food in Recipe

    /*fun getRecipeWithFoods(id: Int?): LiveData<List<RecipeWithFood>>{
        return repository.getRecipeWithFoods(id)
    }*/

}