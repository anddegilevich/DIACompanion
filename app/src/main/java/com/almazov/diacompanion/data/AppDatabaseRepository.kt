package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery

class AppDatabaseRepository(private val appDao: AppDao) {

    // Records

    fun readLastRecords(appType: String): LiveData<List<RecordEntity>> {
        return if (appType == "PCOS") appDao.readLastRecordsPCOS()
        else appDao.readLastRecords()
    }

    fun readDatesPaged(): PagingSource<Int, DateClass> {
        return appDao.readDatesPaged()
    }

    fun readDayRecords(date: String?, appType: String): LiveData<List<RecordEntity>> {
        return if (appType == "PCOS") appDao.readDayRecordsPCOS(date)
        else appDao.readDayRecords(date)
    }

    fun readDayRecords(date: String?, appType: String, filter: String): LiveData<List<RecordEntity>> {
        return if (appType == "PCOS") appDao.readDayRecordsPCOS(date, filter)
        else appDao.readDayRecords(date, filter)
    }

    suspend fun addRecord(recordEntity: RecordEntity): Long {
        val fullDaysCheck = appDao.checkFullDays(recordEntity.date)
        if (fullDaysCheck.contains(true)) recordEntity.fullDay = true
        return appDao.addRecord(recordEntity)
    }

    suspend fun updateRecord(recordEntity: RecordEntity){
        val fullDaysCheck = appDao.checkFullDays(recordEntity.date)
        if (fullDaysCheck.contains(true)) recordEntity.fullDay = true
        appDao.updateRecord(recordEntity)
    }

    suspend fun deleteRecord(recordEntity: RecordEntity){
        appDao.deleteRecord(recordEntity)
    }

    suspend fun deleteAllRecords(){
        appDao.deleteAllRecords()
    }

    fun updateFullDays(date: String?, fullDays: Boolean?){
        appDao.updateFullDays(date, fullDays)
    }
    suspend fun readAllFullDays(): List<String> {
        return appDao.readAllFullDays()
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

    fun checkSugarLevelPrefs(date: String, id: Int?): LiveData<List<String>> {
        return appDao.checkSugarLevelPrefs(date, id)
    }

    suspend fun readAllSugarLevelRecords(): List<RecordSugarLevel> {
        return appDao.readAllSugarLevelRecords()
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

    fun checkInsulinPrefs(date: String, id: Int?): LiveData<List<String>> {
        return appDao.checkInsulinPrefs(date, id)
    }

    suspend fun readAllInsulinRecords(): List<RecordInsulin> {
        return appDao.readAllInsulinRecords()
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

    fun checkMealType(date: String, id: Int?): LiveData<List<String>> {
        return appDao.checkMealType(date, id)
    }

    suspend fun readAllMealRecords(): List<RecordWithMealWithFoods> {
        return appDao.readAllMealRecords()
    }

    suspend fun readPresentDayMealRecords(presentDay: String): List<RecordWithMealWithFoods> {
        return appDao.readPresentDayMealRecords(presentDay)
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

    suspend fun readAllWorkoutRecords(): List<RecordWorkout> {
        return appDao.readAllWorkoutRecords()
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

    suspend fun readAllSleepRecords(): List<RecordSleep> {
        return appDao.readAllSleepRecords()
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

    fun readLastWeightRecordDate(): LiveData<Long?>{
        return appDao.readLastWeightRecordDate()
    }

    suspend fun readAllWeightRecords(): List<RecordWeight> {
        return appDao.readAllWeightRecords()
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

    suspend fun readAllKetoneRecords(): List<RecordKetone> {
        return appDao.readAllKetoneRecords()
    }

    // Food

    fun readFoodPaged(recipe:Boolean): PagingSource<Int, FoodEntity> {
        val x = if (recipe)
        {
            appDao.readFoodPagedWithoutRecipes(recipe)
        }
        else {
            appDao.readFoodPaged()
        }
        return x
    }

    fun readFoodPagedFilter(filter: String, recipe: Boolean,
                            category: String, sortVar: String,
                            direction: String): PagingSource<Int, FoodEntity> {

        var queryWords = ""
        var firstWord = ""
        var stringQuery = ""

        if (filter.isEmpty()) {
            stringQuery = "SELECT * FROM food_table ORDER BY favourite DESC, recipe DESC, $sortVar $direction"
        } else {
            val words = filter.split(" ")
            for (i in 0 until words.count()){
                val word = words[i]
                when (i) {
                    0 -> {
                        firstWord =
                            "SELECT *, 1 AS sort FROM food_table " +
                                    "WHERE name LIKE '$word%' " +
                                    "UNION SELECT *, 2 FROM food_table " +
                                    "WHERE name LIKE '_%$word%' ORDER BY sort ASC, " +
                                    "favourite DESC, recipe DESC, $sortVar $direction"
                    }
                    1 -> queryWords += "WHERE name LIKE '%$word%'"
                    else -> queryWords += " AND name LIKE '%$word%'"
                }
            }
            stringQuery += "SELECT DISTINCT * FROM ($firstWord) $queryWords "
        }
        if (category.isNotEmpty()) stringQuery ="SELECT * FROM ($stringQuery) WHERE category LIKE '$category'"
        if (recipe) stringQuery ="SELECT * FROM ($stringQuery) WHERE recipe NOT LIKE 1"
        val query = SimpleSQLiteQuery(stringQuery)
        return appDao.readFoodPagedFilter(query)
    }

    fun updateFavourite(id: Int?, favourite: Boolean?){
        appDao.updateFavourite(id, favourite)
    }

    // Food in meal

    suspend fun addRecord(foodInMealEntity: FoodInMealEntity){
        appDao.addRecord(foodInMealEntity)
    }

    fun getMealWithFoods(id: Int?): LiveData<List<MealWithFood>>{
        return appDao.getMealWithFoods(id)
    }

    fun getMealWithFoods6HoursAgo(timeInMilli: Long): LiveData<List<MealWithFood>>{
        return appDao.getMealWithFoods6HoursAgo(timeInMilli)
    }

    suspend fun getMealWithFoodsThisDay(day: String): List<MealWithFood> {
        return appDao.getMealWithFoodsThisDay(day)
    }

    suspend fun deleteMealWithFoodsRecord(id: Int?){
        appDao.deleteMealWithFoodsRecord(id)
    }
    suspend fun deleteMealWithRecipesRecord(id: Int?){
        appDao.deleteMealWithRecipesRecord(id)
    }

    // Recipe

    suspend fun addRecord(foodEntity: FoodEntity): Long {
        return appDao.addRecord(foodEntity)
    }

    suspend fun updateRecord(foodEntity: FoodEntity){
        appDao.updateRecord(foodEntity)
    }
    suspend fun deleteRecipeRecord(id: Int?){
        appDao.deleteRecipeRecord(id)
    }

    fun readRecipePaged(): PagingSource<Int, FoodEntity> {
        return appDao.readRecipePaged()
    }

    // Food in Recipe

    suspend fun addRecord(foodInRecipeEntity: FoodInRecipeEntity){
        appDao.addRecord(foodInRecipeEntity)
    }

    fun getRecipeWithFoods(id: Int?): LiveData<List<FoodEntity>> {
        return appDao.getFoodsInRecipe(id)
    }

    fun getWeightsOfFoodsInRecipe(id: Int?): LiveData<List<Double>> {
        return appDao.getWeightsOfFoodsInRecipe(id)
    }

    suspend fun deleteRecipeWithFoodsRecord(id: Int?){
        appDao.deleteRecipeWithFoodsRecord(id)
    }

}