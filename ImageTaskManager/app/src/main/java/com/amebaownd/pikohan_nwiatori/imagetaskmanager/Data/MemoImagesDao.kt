package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface MemoImagesDao {

    @Query("SELECT * FROM MemoImages")
    fun getAll():LiveData<List<MemoImages>>

    @Insert
    fun insert(vararg memoImages: MemoImages)
}