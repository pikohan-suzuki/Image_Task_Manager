package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface TasksDao{


    @Query("SELECT * From Tasks ORDER BY date")
    fun getAll(): LiveData<List<Tasks>>

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY date")
    fun loadTaskAndTaskTagsSortByCreated():LiveData<List<TasksAndTaskTags>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE taskId=(:taskId) ORDER BY date")
    fun loadTaskAndTaskTagsByIdSortByCreated(taskId:Long):LiveData<List<TasksAndTaskTags>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE taskId=(:taskId) ORDER BY date")
    fun loadTaskAndTaskImagesByIdSortByCreated(taskId:Long):LiveData<List<TasksAndTaskImages>>

    @Insert
    fun insert(tasks:Tasks):Long

    @Update
    fun update(task:Tasks)
}