package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class TasksAndTaskImages {
    @Embedded
    lateinit var task:Tasks

    @Relation(parentColumn = "taskId",entityColumn = "taskId")
    lateinit var taskImages:List<TaskImages>
}