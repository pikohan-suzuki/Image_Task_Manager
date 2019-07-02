package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity


@Entity(primaryKeys = ["taskId","imageId"])
data class TaskImages constructor(
    val taskId:Long,
    val imageId:Long
)