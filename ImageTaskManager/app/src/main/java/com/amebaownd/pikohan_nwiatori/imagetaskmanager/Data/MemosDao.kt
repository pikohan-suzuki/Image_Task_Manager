package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction

@Dao
interface MemosDao{

    @Query("SELECT * From Memos")
    fun getAll():LiveData<List<Memos>>

    @Query("SELECT memoId From Memos Where created =(:time)")
    fun getIdByCreated(time:Long):LiveData<Long>


    @Transaction
    @Query("SELECT * FROM Memos ORDER BY title")
    fun loadMemosAndMemoTagsSortByTitle():LiveData<List<MemosAndMemoTags>>

    @Transaction
    @Query("SELECT * FROM Memos ORDER BY created")
    fun loadMemosAndMemoTagsSortByCreated():LiveData<List<MemosAndMemoTags>>

    @Transaction
    @Query("SELECT * FROM Memos WHERE memoId=(:memoId)")
    fun loadMemosAndMemoTagsByMemoId(memoId:Long):LiveData<MemosAndMemoTags>

    @Transaction
    @Query("SELECT * FROM Memos WHERE memoId=(:memoId)")
    fun loadMemosAndMemoImagesByMemoId(memoId:Long):LiveData<MemosAndMemoImages>

    @Insert
    fun insert(memos:Memos):Long

}