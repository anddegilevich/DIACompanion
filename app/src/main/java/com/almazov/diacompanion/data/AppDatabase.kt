package com.almazov.diacompanion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.almazov.diacompanion.data.*

@Database(entities = [RecordEntity::class, SugarLevelEntity::class, InsulinEntity::class,
                     MealEntity::class, WorkoutEntity::class, SleepEntity::class,
                     WeightEntity::class, KetoneEntity::class, FoodEntity::class,
                     FoodInMealEntity::class],
    version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).createFromAsset("database/db_asset.db").build()
                INSTANCE = instance
                return instance

               /* createFromAsset()*/
            }
        }

    }
}