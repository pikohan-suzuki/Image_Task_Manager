package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

@Dao
interface MemoTagsDao {


    @Insert
    fun insert(memoTags:MemoTags)
}