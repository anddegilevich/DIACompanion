package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface AppDao {

    //Records

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(recordEntity: RecordEntity): Long

    @Update
    suspend fun updateRecord(recordEntity: RecordEntity)

    @Delete
    suspend fun deleteRecord(recordEntity: RecordEntity)

    @Query("DELETE FROM record_table")
    suspend fun deleteAllRecords()

    @Query("SELECT * FROM record_table ORDER BY dateInMilli DESC, id DESC LIMIT 10")
    fun readLastRecords(): LiveData<List<RecordEntity>>

    @Query("SELECT * FROM record_table WHERE (category != 'sugar_level_table') AND (category != 'insulin_table')  ORDER BY dateInMilli DESC, id DESC LIMIT 10")
    fun readLastRecordsPCOS(): LiveData<List<RecordEntity>>

    @Query("SELECT DISTINCT date, fullDay FROM record_table ORDER BY dateInMilli DESC")
    fun readDatesPaged(): PagingSource<Int, DateClass>

    @Query("SELECT * FROM record_table WHERE date = :date ORDER BY dateInMilli DESC, id DESC")
    fun readDayRecords(date: String?): LiveData<List<RecordEntity>>

    @Query("SELECT * FROM record_table WHERE (date = :date) AND (category != 'sugar_level_table') AND (category != 'insulin_table') ORDER BY dateInMilli DESC, id DESC")
    fun readDayRecordsPCOS(date: String?): LiveData<List<RecordEntity>>

    @Query("SELECT * FROM record_table WHERE (date = :date) AND (category LIKE :filter) ORDER BY dateInMilli DESC, id DESC")
    fun readDayRecords(date: String?, filter: String): LiveData<List<RecordEntity>>

    @Query("SELECT * FROM record_table WHERE (date = :date) AND (category LIKE :filter) AND (category != 'sugar_level_table') AND (category != 'insulin_table') ORDER BY dateInMilli DESC, id DESC")
    fun readDayRecordsPCOS(date: String?, filter: String): LiveData<List<RecordEntity>>

    @Query("UPDATE record_table SET fullDay = :fullDay WHERE date = :date")
    fun updateFullDays(date: String?, fullDay: Boolean?)

    @Query("SELECT DISTINCT fullDay FROM record_table WHERE date = :date")
    fun checkFullDays(date: String?): List<Boolean>

    @Query("SELECT DISTINCT date FROM record_table WHERE fullDay = 1 ORDER BY dateInMilli ASC")
    suspend fun readAllFullDays(): List<String>

    // SugarLevel

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(sugarLevelEntity: SugarLevelEntity)

    @Update
    suspend fun updateRecord(sugarLevelEntity: SugarLevelEntity)

    @Query("DELETE FROM sugar_level_table WHERE id = :id")
    suspend fun deleteSugarLevelRecord(id: Int?)

    @Query("SELECT * FROM sugar_level_table WHERE id = :id LIMIT 1")
    fun readSugarLevelRecord(id: Int?): LiveData<SugarLevelEntity>

    @Query("SELECT DISTINCT preferences FROM sugar_level_table WHERE id IN (SELECT id FROM " +
            "record_table WHERE (date = :date) AND (category = 'sugar_level_table') " +
            "AND (id != :id))")
    fun checkSugarLevelPrefs(date: String, id: Int?): LiveData<List<String>>

    @Query("SELECT * FROM record_table WHERE category = 'sugar_level_table' ORDER BY dateInMilli ASC")
    suspend fun readAllSugarLevelRecords(): List<RecordSugarLevel>

    // Insulin

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(insulinEntity: InsulinEntity)

    @Update
    suspend fun updateRecord(insulinEntity: InsulinEntity)

    @Query("DELETE FROM insulin_table WHERE id = :id")
    suspend fun deleteInsulinRecord(id: Int?)

    @Query("SELECT * FROM insulin_table WHERE id = :id LIMIT 1")
    fun readInsulinRecord(id: Int?): LiveData<InsulinEntity>

    @Query("SELECT DISTINCT preferences FROM insulin_table WHERE id IN (SELECT id FROM " +
            "record_table WHERE (date = :date) AND (category = 'insulin_table') " +
            "AND (id != :id))")
    fun checkInsulinPrefs(date: String, id: Int?): LiveData<List<String>>

    @Query("SELECT * FROM record_table WHERE category = 'insulin_table' ORDER BY dateInMilli ASC")
    suspend fun readAllInsulinRecords(): List<RecordInsulin>

    // Meal

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(mealEntity: MealEntity)

    @Update
    suspend fun updateRecord(mealEntity: MealEntity)

    @Query("DELETE FROM meal_table WHERE idMeal = :id")
    suspend fun deleteMealRecord(id: Int?)

    @Query("SELECT * FROM meal_table WHERE idMeal = :id LIMIT 1")
    fun readMealRecord(id: Int?): LiveData<MealEntity>

    @Query("SELECT DISTINCT type FROM meal_table WHERE idMeal IN (SELECT id FROM " +
            "record_table WHERE (date = :date) AND (category = 'meal_table') " +
            "AND (id != :id))")
    fun checkMealType(date: String, id: Int?): LiveData<List<String>>

    @Transaction
    @Query("SELECT * FROM record_table WHERE category = 'meal_table' ORDER BY dateInMilli ASC")
    fun readAllMealRecords(): List<RecordWithMealWithFoods>

    @Transaction
    @Query("SELECT * FROM record_table WHERE category = 'meal_table' AND date = :presentDate ORDER BY dateInMilli ASC")
    fun readPresentDayMealRecords(presentDate: String): List<RecordWithMealWithFoods>

    @Transaction
    @Query("SELECT * FROM record_table WHERE category = 'meal_table' AND " +
            "(dateInMilli > (:time + 0.75 * 60 * 60 * 1000)) AND" +
            "(dateInMilli < (:time + 1.25 * 60 * 60 * 1000)) ORDER BY dateInMilli ASC")
    fun readMealsBeforeSugarLevel(time: Long): List<RecordWithMealWithFoods>

    // Workout

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(workoutEntity: WorkoutEntity)

    @Update
    suspend fun updateRecord(workoutEntity: WorkoutEntity)

    @Query("DELETE FROM workout_table WHERE id = :id")
    suspend fun deleteWorkoutRecord(id: Int?)

    @Query("SELECT * FROM workout_table WHERE id = :id LIMIT 1")
    fun readWorkoutRecord(id: Int?): LiveData<WorkoutEntity>

    @Query("SELECT * FROM record_table WHERE category = 'workout_table' ORDER BY dateInMilli ASC")
    suspend fun readAllWorkoutRecords(): List<RecordWorkout>

    // Sleep

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(sleepEntity: SleepEntity)

    @Update
    suspend fun updateRecord(sleepEntity: SleepEntity)

    @Query("DELETE FROM sleep_table WHERE id = :id")
    suspend fun deleteSleepRecord(id: Int?)

    @Query("SELECT * FROM sleep_table WHERE id = :id LIMIT 1")
    fun readSleepRecord(id: Int?): LiveData<SleepEntity>

    @Query("SELECT * FROM record_table WHERE category = 'sleep_table' ORDER BY dateInMilli ASC")
    suspend fun readAllSleepRecords(): List<RecordSleep>

    // Weight

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(weightEntity: WeightEntity)

    @Update
    suspend fun updateRecord(weightEntity: WeightEntity)

    @Query("DELETE FROM weight_table WHERE id = :id")
    suspend fun deleteWeightRecord(id: Int?)

    @Query("SELECT * FROM weight_table WHERE id = :id LIMIT 1")
    fun readWeightRecord(id: Int?): LiveData<WeightEntity>

    @Query("SELECT dateInMilli FROM record_table WHERE category = 'weight_table' ORDER BY dateInMilli DESC LIMIT 1")
    fun readLastWeightRecordDate(): LiveData<Long?>

    @Query("SELECT * FROM record_table WHERE category = 'weight_table' ORDER BY dateInMilli ASC")
    suspend fun readAllWeightRecords(): List<RecordWeight>

    // Ketone

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(ketoneEntity: KetoneEntity)

    @Update
    suspend fun updateRecord(ketoneEntity: KetoneEntity)

    @Query("DELETE FROM ketone_table WHERE id = :id")
    suspend fun deleteKetoneRecord(id: Int?)

    @Query("SELECT * FROM ketone_table WHERE id = :id LIMIT 1")
    fun readKetoneRecord(id: Int?): LiveData<KetoneEntity>

    @Query("SELECT * FROM record_table WHERE category = 'ketone_table' ORDER BY dateInMilli ASC")
    suspend fun readAllKetoneRecords(): List<RecordKetone>

    // Food

    @Query("SELECT * FROM food_table ORDER BY favourite DESC, recipe DESC, name")
    fun readFoodPaged(): PagingSource<Int, FoodEntity>

    @Query("SELECT * FROM food_table WHERE recipe != :recipe ORDER BY favourite DESC, recipe DESC, name")
    fun readFoodPagedWithoutRecipes(recipe: Boolean): PagingSource<Int, FoodEntity>

    @RawQuery(observedEntities = [FoodEntity::class])
    fun readFoodPagedFilter(query: SupportSQLiteQuery): PagingSource<Int, FoodEntity>

    @Query("UPDATE food_table SET favourite = :favourite WHERE idFood = :id")
    fun updateFavourite(id: Int?, favourite: Boolean?)

    // Food in meal

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(foodInMealEntity: FoodInMealEntity)

    @Query("""
        SELECT * FROM meal_table 
        INNER JOIN food_in_meal_table ON (meal_table.idMeal = food_in_meal_table.idMeal)
        AND (food_in_meal_table.idMeal = :id)
        INNER JOIN food_table ON (food_table.idFood = food_in_meal_table.idFood)
        """)
    fun getMealWithFoods(id: Int?): LiveData<List<MealWithFood>>

    @Query("""
        SELECT * FROM meal_table 
        INNER JOIN food_in_meal_table ON (meal_table.idMeal = food_in_meal_table.idMeal)
        AND (food_in_meal_table.idMeal = (SELECT id FROM record_table WHERE (category = "meal_table") AND (dateInMilli < :timeInMilli) AND (dateInMilli > :timeInMilli - 21600000)))
        INNER JOIN food_table ON (food_table.idFood = food_in_meal_table.idFood)
        """)
    suspend fun getMealWithFoods6HoursAgo(timeInMilli: Long): List<MealWithFood>

    @Query("""
        SELECT * FROM meal_table 
        INNER JOIN food_in_meal_table ON (meal_table.idMeal = food_in_meal_table.idMeal)
        AND (food_in_meal_table.idMeal = (SELECT id FROM record_table WHERE (category = "meal_table") AND (dateInMilli < :timeInMilli) AND (dateInMilli > :timeInMilli - 43200000)))
        INNER JOIN food_table ON (food_table.idFood = food_in_meal_table.idFood)
        """)
    suspend fun getMealWithFoods12HoursAgo(timeInMilli: Long): List<MealWithFood>

    @Query("""
        SELECT * FROM meal_table 
        INNER JOIN food_in_meal_table ON (meal_table.idMeal = food_in_meal_table.idMeal)
        AND (food_in_meal_table.idMeal = (SELECT id FROM record_table WHERE (category = "meal_table") AND (date = :day)))
        INNER JOIN food_table ON (food_table.idFood = food_in_meal_table.idFood)
        """)
    suspend fun getMealWithFoodsThisDay(day: String): List<MealWithFood>

    @Query("DELETE FROM food_in_meal_table WHERE idMeal = :id")
    suspend fun deleteMealWithFoodsRecord(id: Int?)

    @Query("DELETE FROM food_in_meal_table WHERE idFood = :id")
    suspend fun deleteMealWithRecipesRecord(id: Int?)

    // Recipe

    @Query("SELECT * FROM food_table WHERE recipe = :recipe ORDER BY favourite DESC,name ASC")
    fun readRecipePaged(recipe: Boolean = true): PagingSource<Int, FoodEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(foodEntity: FoodEntity): Long

    @Update
    suspend fun updateRecord(foodEntity: FoodEntity)

    @Query("DELETE FROM food_table WHERE idFood = :id")
    suspend fun deleteRecipeRecord(id: Int?)

    // Food In Recipe

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(foodInRecipeEntity: FoodInRecipeEntity)

    @Query("""SELECT * FROM food_table WHERE idFood IN 
        (SELECT idFood  FROM food_in_recipe_table WHERE (idRecipe = :id))""")
    fun getFoodsInRecipe(id: Int?): LiveData<List<FoodEntity>>

    @Query("""SELECT weight FROM food_in_recipe_table WHERE (idRecipe = :id)""")
    fun getWeightsOfFoodsInRecipe(id: Int?): LiveData<List<Double>>

    @Query("DELETE FROM food_in_recipe_table WHERE idRecipe LIKE :id")
    suspend fun deleteRecipeWithFoodsRecord(id: Int?)

    //Questionnaire
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveQuestionnaire(questionnaireEntity: QuestionnaireEntity)

    @Query("SELECT * FROM questionnaire_table LIMIT 1")
    fun getQuestionnaire(): QuestionnaireEntity
}