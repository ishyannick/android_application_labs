package com.example.studyplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudyTaskEntity::class], version = 1, exportSchema = true)
abstract class StudyPlannerDatabase : RoomDatabase() {
    abstract fun studyTaskDao(): StudyTaskDao

    companion object {
        @Volatile
        private var instance: StudyPlannerDatabase? = null

        fun getInstance(context: Context): StudyPlannerDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StudyPlannerDatabase::class.java,
                    "study_planner.db"
                ).build().also { instance = it }
            }
    }
}
