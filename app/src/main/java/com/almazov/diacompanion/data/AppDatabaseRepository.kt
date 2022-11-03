package com.almazov.diacompanion.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource

class AppDatabaseRepository(private val appDao: AppDao) {

    /*val readAllData: LiveData<List<RecordEntity>> = appDao.readAllData()*/
    val readAllPaged: PagingSource<Int, RecordEntity> = appDao.readAllPaged()

    //Records
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

    /*fun filterDatabase(filter: String): LiveData<List<RecordEntity>> {
        return appDao.filterDatabase(filter)
    }*/

    fun filterPaged(filter: String): PagingSource<Int, RecordEntity> {
        return appDao.filterPaged(filter)
    }

    //SugarLevel
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

    //Insulin
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

    //Meal
    suspend fun addRecord(mealEntity: MealEntity){
        appDao.addRecord(mealEntity)
    }

    fun readMealRecord(id: Int?): LiveData<MealEntity>{
        return appDao.readMealRecord(id)
    }

    suspend fun updateRecord(mealEntity: MealEntity){
        appDao.updateRecord(mealEntity)
    }
    suspend fun deleteMealRecord(id: Int?){
        appDao.deleteMealRecord(id)
    }

    //Workout
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

    //Sleep
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

    //Weight
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

    //Ketone
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

}