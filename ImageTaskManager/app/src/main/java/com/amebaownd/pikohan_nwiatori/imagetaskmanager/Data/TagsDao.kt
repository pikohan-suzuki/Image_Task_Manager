package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface TagsDao {
    @Query("SELECT * FROM Tags")
    fun getAllTags():LiveData<List<Tags>>

    @Query("SELECT * FROM Tags WHERE tagId=(:tagId)")
    fun getTagByTagId(tagId:Long):LiveData<Tags>

    @Query("SELECT COUNT(*) FROM Tags WHERE name=(:name)")
    fun getCountByName(name:String):LiveData<Int>

    @Query("SELECT * FROM Tags WHERE name LIKE (:key)")
    fun getTagsByKey(key:String):LiveData<List<Tags>>

    @Insert(onConflict = OnConflictStrategy.ROLLBACK)
    fun insert(vararg tag:Tags)

    @Update
    fun update(tag:Tags)
}