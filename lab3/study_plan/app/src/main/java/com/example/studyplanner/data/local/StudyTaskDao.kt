package com.example.studyplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY dateTime ASC")
    fun observeAll(): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<StudyTaskEntity?>

    @Query("SELECT * FROM study_tasks")
    suspend fun getAllOnce(): List<StudyTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: StudyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<StudyTaskEntity>)

    @Update
    suspend fun update(task: StudyTaskEntity)

    @Delete
    suspend fun delete(task: StudyTaskEntity)

    @Query("DELETE FROM study_tasks WHERE id = :id")
    suspend fun deleteById(id: String)
}
