package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity

@Entity(primaryKeys = ["memoId","tagId"])
data class MemoTags constructor(
    val memoId:Long,
    val tagId:Long
)