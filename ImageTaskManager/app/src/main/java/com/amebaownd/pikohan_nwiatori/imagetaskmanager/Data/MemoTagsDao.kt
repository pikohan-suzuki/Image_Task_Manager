package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction

@Dao
interface MemoTagsDao {


    @Insert
    fun insert( memoTags:MemoTags)

    @Insert
    fun insertAll(memoTags:List<MemoTags>)

    @Query("DELETE FROM MemoTags WHERE memoId=(:memoId)")
    fun delete(memoId:Long)

    @Transaction
    fun update(memoId: Long, memoTags: List<MemoTags>){
        delete(memoId)
        insertAll(memoTags)
    }
}