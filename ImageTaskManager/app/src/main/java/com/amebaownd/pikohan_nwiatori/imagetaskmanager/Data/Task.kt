package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import java.sql.Time

data class Task(
    var title: String,
    var tagIds: MutableList<Long>,
    var imageIds: MutableList<Long>,
    var memo: String,
    var timeLimit: Time,
    var isActive: Boolean
)