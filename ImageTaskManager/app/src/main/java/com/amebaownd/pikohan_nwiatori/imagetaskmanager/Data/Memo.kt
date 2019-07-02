package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import java.sql.Time

data class Memo(
    var title: String,
    var tagIds: MutableList<Long>,
    var imageIds: MutableList<Long>,
    var memo: String,
    var created: Time
)