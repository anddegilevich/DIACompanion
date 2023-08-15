package com.almazov.diacompanion.data

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.*
import com.almazov.diacompanion.meal.FoodInMealItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AppDatabaseViewModel(application: Application) : AndroidViewModel(application) {

    fun readDatesPaged(): Flow<PagingData<DateClass>> {
        return Pager(
            PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,
                maxSize = 60
            )
        ) {
            repository.readDatesPaged()
        }.flow
    }

    private val repository: AppDatabaseRepository

    init {
        val appDao = AppDatabase.getDatabase(application).appDao()
        repository = AppDatabaseRepository(appDao)
    }

    // Records

    fun readLastRecords(appType: String): LiveData<List<RecordEntity>> {
        return repository.readLastRecords(appType)
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

    fun readDayRecords(date: String?, appType: String): LiveData<List<RecordEntity>> {
        return repository.readDayRecords(date, appType)
    }

    fun readDayRecords(
        date: String?,
        appType: String,
        filter: String
    ): LiveData<List<RecordEntity>> {
        return repository.readDayRecords(date, appType, filter)
    }

    fun updateFullDays(date: String?, fullDay: Boolean?) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateFullDays(date, fullDay)
        }
    }

    suspend fun readAllFullDays(): List<String> {
        return repository.readAllFullDays()
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

    fun readSugarLevelRecord(id: Int?): LiveData<SugarLevelEntity> {
        return repository.readSugarLevelRecord(id)
    }

    fun deleteSugarLevelRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSugarLevelRecord(id)
        }
    }

    fun checkSugarLevelPrefs(date: String, id: Int?): LiveData<List<String>> {
        return repository.checkSugarLevelPrefs(date, id)
    }

    suspend fun readAllSugarLevelRecords(): List<RecordSugarLevel> {
        return repository.readAllSugarLevelRecords()
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

    fun readInsulinRecord(id: Int?): LiveData<InsulinEntity> {
        return repository.readInsulinRecord(id)
    }

    fun deleteInsulinRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteInsulinRecord(id)
        }
    }

    fun checkInsulinPrefs(date: String, id: Int?): LiveData<List<String>> {
        return repository.checkInsulinPrefs(date, id)
    }

    suspend fun readAllInsulinRecords(): List<RecordInsulin> {
        return repository.readAllInsulinRecords()
    }

    // Meal

    fun addRecord(
        recordEntity: RecordEntity,
        mealEntity: MealEntity,
        foodList: MutableList<FoodInMealItem>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(recordEntity)
            mealEntity.idMeal = id.toInt()
            repository.addRecord(mealEntity)
            for (food in foodList) {
                val foodInMealEntity =
                    FoodInMealEntity(id.toInt(), food.foodEntity.idFood!!, food.weight)
                repository.addRecord(foodInMealEntity)
            }
        }
    }

    fun updateRecord(
        recordEntity: RecordEntity,
        mealEntity: MealEntity,
        foodList: MutableList<FoodInMealItem>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(recordEntity)
            repository.updateRecord(mealEntity)
            repository.deleteMealWithFoodsRecord(recordEntity.id)
            for (food in foodList) {
                val foodInMealEntity =
                    FoodInMealEntity(recordEntity.id!!, food.foodEntity.idFood!!, food.weight)
                repository.addRecord(foodInMealEntity)
            }
        }
    }

    fun deleteMealRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMealRecord(id)
            repository.deleteMealWithFoodsRecord(id)
        }
    }

    fun checkMealType(date: String, id: Int?): LiveData<List<String>> {
        return repository.checkMealType(date, id)
    }

    suspend fun readAllMealRecords(): List<RecordWithMealWithFoods> {
        return repository.readAllMealRecords()
    }

    suspend fun readPresentDayMealRecords(presentDay: String): List<RecordWithMealWithFoods> {
        return repository.readPresentDayMealRecords(presentDay)
    }

    suspend fun readMealsBeforeSugarLevel(time: Long): List<RecordWithMealWithFoods> {
        return repository.readMealsBeforeSugarLevel(time)
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

    fun readWorkoutRecord(id: Int?): LiveData<WorkoutEntity> {
        return repository.readWorkoutRecord(id)
    }

    fun deleteWorkoutRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkoutRecord(id)
        }
    }

    suspend fun readAllWorkoutRecords(): List<RecordWorkout> {
        return repository.readAllWorkoutRecords()
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

    fun readSleepRecord(id: Int?): LiveData<SleepEntity> {
        return repository.readSleepRecord(id)
    }

    fun deleteSleepRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSleepRecord(id)
        }
    }

    suspend fun readAllSleepRecords(): List<RecordSleep> {
        return repository.readAllSleepRecords()
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

    fun readWeightRecord(id: Int?): LiveData<WeightEntity> {
        return repository.readWeightRecord(id)
    }

    fun deleteWeightRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWeightRecord(id)
        }
    }

    fun readLastWeightRecordDate(): LiveData<Long?> {
        return repository.readLastWeightRecordDate()
    }

    suspend fun readAllWeightRecords(): List<RecordWeight> {
        return repository.readAllWeightRecords()
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

    fun readKetoneRecord(id: Int?): LiveData<KetoneEntity> {
        return repository.readKetoneRecord(id)
    }

    fun deleteKetoneRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteKetoneRecord(id)
        }
    }

    suspend fun readAllKetoneRecords(): List<RecordKetone> {
        return repository.readAllKetoneRecords()
    }

    // Food

    fun readFoodPaged(recipe: Boolean): Flow<PagingData<FoodEntity>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            repository.readFoodPaged(recipe)
        }.flow
    }

    fun readFoodPagedFilter(
        filter: String, recipe: Boolean, category: String, sortVar: String,
        direction: String
    ): Flow<PagingData<FoodEntity>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            repository.readFoodPagedFilter(filter, recipe, category, sortVar, direction)
        }.flow
    }

    fun updateFavourite(id: Int?, favourite: Boolean?) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateFavourite(id, favourite)
        }
    }

    // Food in meal

    fun getMealWithFoods(id: Int?): LiveData<List<MealWithFood>> {
        return repository.getMealWithFoods(id)
    }

    fun getMealWithFoods6HoursAgo(timeInMilli: Long): LiveData<List<MealWithFood>> {
        return repository.getMealWithFoods6HoursAgo(timeInMilli)
    }

    suspend fun getMealWithFoodsThisDay(day: String): List<MealWithFood> {
        return repository.getMealWithFoodsThisDay(day)
    }

    // Recipe

    fun addRecord(foodEntity: FoodEntity, foodList: MutableList<FoodInMealItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addRecord(foodEntity)
            for (food in foodList) {
                val foodInRecipeEntity =
                    FoodInRecipeEntity(id.toInt(), food.foodEntity.idFood!!, food.weight)
                repository.addRecord(foodInRecipeEntity)
            }
        }
    }

    fun updateRecord(foodEntity: FoodEntity, foodList: MutableList<FoodInMealItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(foodEntity)
            repository.deleteRecipeWithFoodsRecord(foodEntity.idFood)
            for (food in foodList) {
                val foodInRecipeEntity =
                    FoodInRecipeEntity(foodEntity.idFood!!, food.foodEntity.idFood!!, food.weight)
                repository.addRecord(foodInRecipeEntity)
            }
        }
    }

    fun deleteRecipeRecord(id: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecipeRecord(id)
            repository.deleteRecipeWithFoodsRecord(id)
            repository.deleteMealWithRecipesRecord(id)
        }
    }

    fun readRecipePaged(): Flow<PagingData<FoodEntity>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            repository.readRecipePaged()
        }.flow
    }

    //Food in Recipe

    fun getRecipeWithFoods(id: Int?): LiveData<List<FoodEntity>> {
        return repository.getRecipeWithFoods(id)
    }

    fun getWeightsOfFoodsInRecipe(id: Int?): LiveData<List<Double>> {
        return repository.getWeightsOfFoodsInRecipe(id)
    }

    //Questionnaire
    fun saveQuestionnaire(questionnaireEntity: QuestionnaireEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveQuestionnaire(questionnaireEntity)
        }
    }

    fun getQuestionnaire(): QuestionnaireEntity {
        return repository.getQuestionnaire()
    }

}