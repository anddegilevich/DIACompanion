package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery

class AppDatabaseRepository(private val appDao: AppDao) {

    // Records

    val readLastRecords: LiveData<List<RecordEntity>> = appDao.readLastRecords()

    fun readDatesPaged(): PagingSource<Int, DateClass> {
        return appDao.readDatesPaged()
    }

    fun readDayRecords(date: String): LiveData<List<RecordEntity>> {
        return appDao.readDayRecords(date)
    }

    fun readDayRecords(date: String, filter: String): LiveData<List<RecordEntity>> {
        return appDao.readDayRecords(date, filter)
    }

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

    // SugarLevel

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

    // Insulin

    suspend fun addRecord(insulinEntity: InsulinEntity){
        appDao.addRecord(insulinEntity)
    }

    fun readInsulinRecord(id: Int?): LiveData<InsulinEntity>{
        return appDao.readInsulinRecord(id)
    }

    suspend fun updateRecord(insulinEntity: InsulinEntity){
        appDao.updateRecord(insulinEntity)
    }
    suspend fun deleteInsulinRecord(id: Int?){
        appDao.deleteInsulinRecord(id)
    }

    // Meal

    suspend fun addRecord(mealEntity: MealEntity){
        appDao.addRecord(mealEntity)
    }

    suspend fun updateRecord(mealEntity: MealEntity){
        appDao.updateRecord(mealEntity)
    }
    suspend fun deleteMealRecord(id: Int?){
        appDao.deleteMealRecord(id)
    }

    // Workout

    suspend fun addRecord(workoutEntity: WorkoutEntity){
        appDao.addRecord(workoutEntity)
    }

    fun readWorkoutRecord(id: Int?): LiveData<WorkoutEntity>{
        return appDao.readWorkoutRecord(id)
    }

    suspend fun updateRecord(workoutEntity: WorkoutEntity){
        appDao.updateRecord(workoutEntity)
    }
    suspend fun deleteWorkoutRecord(id: Int?){
        appDao.deleteWorkoutRecord(id)
    }

    // Sleep

    suspend fun addRecord(sleepEntity: SleepEntity){
        appDao.addRecord(sleepEntity)
    }

    fun readSleepRecord(id: Int?): LiveData<SleepEntity>{
        return appDao.readSleepRecord(id)
    }

    suspend fun updateRecord(sleepEntity: SleepEntity){
        appDao.updateRecord(sleepEntity)
    }
    suspend fun deleteSleepRecord(id: Int?){
        appDao.deleteSleepRecord(id)
    }

    // Weight

    suspend fun addRecord(weightEntity: WeightEntity){
        appDao.addRecord(weightEntity)
    }

    fun readWeightRecord(id: Int?): LiveData<WeightEntity>{
        return appDao.readWeightRecord(id)
    }

    suspend fun updateRecord(weightEntity: WeightEntity){
        appDao.updateRecord(weightEntity)
    }
    suspend fun deleteWeightRecord(id: Int?){
        appDao.deleteWeightRecord(id)
    }

    // Ketone

    suspend fun addRecord(ketoneEntity: KetoneEntity){
        appDao.addRecord(ketoneEntity)
    }

    fun readKetoneRecord(id: Int?): LiveData<KetoneEntity>{
        return appDao.readKetoneRecord(id)
    }

    suspend fun updateRecord(ketoneEntity: KetoneEntity){
        appDao.updateRecord(ketoneEntity)
    }
    suspend fun deleteKetoneRecord(id: Int?){
        appDao.deleteKetoneRecord(id)
    }

    // Food

    val readFoodPaged: PagingSource<Int, FoodEntity> = appDao.readFoodPaged()

    fun readFoodPagedFilter(filter: String): PagingSource<Int, FoodEntity> {

        var queryWords = ""
        val words = filter.split(" ")
        for (word in words){
            queryWords += if (queryWords.isEmpty())
                "name LIKE '%$word%'"
            else
                "AND name LIKE '%$word%'"
        }
        val stringQuery =
            """SELECT * FROM food_table WHERE $queryWords ORDER BY favourite DESC,name ASC"""
        val query = SimpleSQLiteQuery(stringQuery)
        return appDao.readFoodPagedFilter(query)
    }

    fun updateFavourite(id: Int?, favourite: Int?){
        appDao.updateFavourite(id, favourite)
    }

    // Food in meal

    suspend fun addRecord(foodInMealEntity: FoodInMealEntity){
        appDao.addRecord(foodInMealEntity)
    }

    fun getMealWithFoods(id: Int?): LiveData<List<MealWithFood>>{
        return appDao.getMealWithFoods(id)
    }

    suspend fun deleteMealWithFoodsRecord(id: Int?){
        appDao.deleteMealWithFoodsRecord(id)
    }
}