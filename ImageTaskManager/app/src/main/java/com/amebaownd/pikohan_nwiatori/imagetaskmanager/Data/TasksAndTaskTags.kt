package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class TasksAndTaskTags {
    @Embedded
    lateinit var task:Tasks

    @Relation(parentColumn = "taskId",entityColumn = "taskId")
    lateinit var taskTags:List<TaskTags>
}
