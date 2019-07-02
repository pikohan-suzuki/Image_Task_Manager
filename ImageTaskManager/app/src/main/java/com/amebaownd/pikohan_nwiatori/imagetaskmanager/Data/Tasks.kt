package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.sql.Time


@Entity
data class Tasks constructor(
    @PrimaryKey(autoGenerate = true)
    val taskId:Long,
    val title:String,
    val date:Long,
    val memo:String,
    val is_active:Boolean
)