package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey



@Entity(primaryKeys = ["taskId","order"],
    foreignKeys = [ForeignKey(entity = Tasks::class
        ,parentColumns = ["taskId"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE)])
 class TaskImages (
    var taskId:Long =0,
    var order:Int=0,
    var uri: String=""
)