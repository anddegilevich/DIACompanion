package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*

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

    /*@Query("SELECT * FROM record_table ORDER BY dateInMilli DESC")
    fun readAllData(): LiveData<List<RecordEntity>>*/

    @Query("SELECT * FROM record_table ORDER BY dateInMilli DESC, id DESC")
    fun readAllPaged(): PagingSource<Int, RecordEntity>

    /*@Query("SELECT * FROM record_table WHERE category LIKE :filter")
    fun filterDatabase(filter: String): LiveData<List<RecordEntity>>*/

    @Query("SELECT * FROM record_table WHERE category LIKE :filter ORDER BY dateInMilli DESC, id DESC")
    fun filterPaged(filter: String): PagingSource<Int, RecordEntity>

    // SugarLevel

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(sugarLevelEntity: SugarLevelEntity)

    @Update
    suspend fun updateRecord(sugarLevelEntity: SugarLevelEntity)

    @Query("DELETE FROM sugar_level_table WHERE id LIKE :id")
    suspend fun deleteSugarLevelRecord(id: Int?)

    @Query("SELECT * FROM sugar_level_table WHERE id LIKE :id LIMIT 1")
    fun readSugarLevelRecord(id: Int?): LiveData<SugarLevelEntity>

    // Insulin

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(insulinEntity: InsulinEntity)

    @Update
    suspend fun updateRecord(insulinEntity: InsulinEntity)

    @Query("DELETE FROM insulin_table WHERE id LIKE :id")
    suspend fun deleteInsulinRecord(id: Int?)

    @Query("SELECT * FROM insulin_table WHERE id LIKE :id LIMIT 1")
    fun readInsulinRecord(id: Int?): LiveData<InsulinEntity>

    // Meal

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(mealEntity: MealEntity)

    @Update
    suspend fun updateRecord(mealEntity: MealEntity)

    @Query("DELETE FROM meal_table WHERE idMeal LIKE :id")
    suspend fun deleteMealRecord(id: Int?)

    @Query("SELECT * FROM meal_table WHERE idMeal LIKE :id LIMIT 1")
    fun readMealRecord(id: Int?): LiveData<MealEntity>

    // Workout

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(workoutEntity: WorkoutEntity)

    @Update
    suspend fun updateRecord(workoutEntity: WorkoutEntity)

    @Query("DELETE FROM workout_table WHERE id LIKE :id")
    suspend fun deleteWorkoutRecord(id: Int?)

    @Query("SELECT * FROM workout_table WHERE id LIKE :id LIMIT 1")
    fun readWorkoutRecord(id: Int?): LiveData<WorkoutEntity>

    // Sleep

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(sleepEntity: SleepEntity)

    @Update
    suspend fun updateRecord(sleepEntity: SleepEntity)

    @Query("DELETE FROM sleep_table WHERE id LIKE :id")
    suspend fun deleteSleepRecord(id: Int?)

    @Query("SELECT * FROM sleep_table WHERE id LIKE :id LIMIT 1")
    fun readSleepRecord(id: Int?): LiveData<SleepEntity>

    // Weight

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(weightEntity: WeightEntity)

    @Update
    suspend fun updateRecord(weightEntity: WeightEntity)

    @Query("DELETE FROM weight_table WHERE id LIKE :id")
    suspend fun deleteWeightRecord(id: Int?)

    @Query("SELECT * FROM weight_table WHERE id LIKE :id LIMIT 1")
    fun readWeightRecord(id: Int?): LiveData<WeightEntity>

    // Ketone

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(ketoneEntity: KetoneEntity)

    @Update
    suspend fun updateRecord(ketoneEntity: KetoneEntity)

    @Query("DELETE FROM ketone_table WHERE id LIKE :id")
    suspend fun deleteKetoneRecord(id: Int?)

    @Query("SELECT * FROM ketone_table WHERE id LIKE :id LIMIT 1")
    fun readKetoneRecord(id: Int?): LiveData<KetoneEntity>

    // Food

    @Query("SELECT * FROM food_table ORDER BY name ASC")
    fun readFoodPaged(): PagingSource<Int, FoodEntity>

    @Query("""
        SELECT * FROM (SELECT * FROM food_table WHERE name LIKE :filter+'%'
            ORDER BY name ASC LIMIT 20)
        UNION
        SELECT * FROM (SELECT * FROM food_table WHERE name LIKE '%'+:filter
            ORDER BY name ASC LIMIT 5)
        UNION
        SELECT * FROM (SELECT * FROM food_table WHERE name LIKE '%'+:filter+'%'
            ORDER BY name ASC LIMIT 5)
    """)
    fun readFoodPagedFilterBegin(filter: String): PagingSource<Int, FoodEntity>

    /*@Query("SELECT * FROM food_table WHERE name LIKE '%'+:filter ORDER BY name ASC LIMIT 5")
    suspend fun readFoodPagedFilterEnd(filter: String): LiveData<List<FoodEntity>>

    @Query("SELECT * FROM food_table WHERE name LIKE '%'+:filter+'%'ORDER BY name ASC LIMIT 5")
    suspend fun readFoodPagedFilterInside(filter: String): LiveData<List<FoodEntity>>*/

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

    @Query("DELETE FROM food_in_meal_table WHERE idMeal LIKE :id")
    suspend fun deleteMealWithFoodsRecord(id: Int?)
}