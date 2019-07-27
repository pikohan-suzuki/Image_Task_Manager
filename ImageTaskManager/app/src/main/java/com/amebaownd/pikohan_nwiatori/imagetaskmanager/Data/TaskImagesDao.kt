package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction

@Dao
interface TaskImagesDao {

    @Query("SELECT * FROM TaskImages")
    fun getAll(): LiveData<List<TaskImages>>

    @Insert
    fun insert( taskImages: TaskImages)

    @Insert
    fun insertAll(taskImages:List<TaskImages>)

    @Query("DELETE FROM taskimages WHERE taskId=(:taskId)")
    fun delete(taskId:Long)

    @Transaction
    fun update(taskId: Long, taskImages: List<TaskImages>){
        delete(taskId)
        insertAll(taskImages)
    }
}