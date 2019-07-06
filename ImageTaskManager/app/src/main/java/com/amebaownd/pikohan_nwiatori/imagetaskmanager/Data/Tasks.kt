package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.sql.Time

@Entity
data class Tasks constructor(
    @PrimaryKey(autoGenerate = true)
    var taskId:Long=0,
    var title:String="",
    var date:Long=0,
    var memo:String="",
    var isActive:Boolean = true
)