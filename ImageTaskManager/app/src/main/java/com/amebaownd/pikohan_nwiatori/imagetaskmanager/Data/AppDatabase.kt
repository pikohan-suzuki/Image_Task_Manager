package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(
    Images::class,
    MemoImages::class,
    Memos::class,
    MemoTags::class,
    Tags::class,
    TaskImages::class,
    Tasks::class,
    TaskTags::class
),version = 2)
abstract class AppDatabase :RoomDatabase() {
    abstract fun imagesDao():ImagesDao
    abstract fun memoImagesDao():MemoImagesDao
    abstract fun memosDao():MemosDao
    abstract fun memoTagsDao():MemoTagsDao
    abstract fun tagsDao():TagsDao
    abstract fun taskImagesDao():TaskImagesDao
    abstract fun tasksDao():TasksDao
    abstract fun taskTagsDao():TaskTagsDao
}