package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(primaryKeys = ["taskId","tagId"],
    foreignKeys = [ForeignKey(entity = Tasks::class
        ,parentColumns = ["taskId"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Tags::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE)])
data class TaskTags constructor(
    val tagId:Long,
    val taskId:Long
)