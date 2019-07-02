package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(primaryKeys = ["taskId","tagId"])
data class TaskTags constructor(
    val tagId:Long,
    val taskId:Long
)