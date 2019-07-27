package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction

@Dao
interface TaskTagsDao {

    @Insert
    fun insert( taskTags:TaskTags)

    @Insert
    fun insertAll(taskTags:List<TaskTags>)

    @Query("DELETE FROM TaskTags WHERE taskId=(:taskId)")
    fun delete(taskId:Long)

    @Transaction
    fun update(taskId: Long, taskTags: List<TaskTags>){
        delete(taskId)
        insertAll(taskTags)
    }
}