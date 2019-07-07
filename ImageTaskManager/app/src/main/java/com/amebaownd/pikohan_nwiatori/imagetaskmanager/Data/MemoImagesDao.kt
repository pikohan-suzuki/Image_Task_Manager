package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface MemoImagesDao {

    @Query("SELECT * FROM MemoImages")
    fun getAll():LiveData<List<MemoImages>>

    @Insert
    fun insert( memoImages: MemoImages)

    @Insert
    fun insertAll(memoImages:List<MemoImages>)

    @Query("DELETE FROM MemoImages WHERE memoId=(:memoId)")
    fun delete(memoId:Long)

    @Transaction
    fun update(memoId: Long, memoImages: List<MemoImages>){
        delete(memoId)
        insertAll(memoImages)
    }
}