package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity

@Entity(primaryKeys = ["memoId", "imageId"])
data class MemoImages constructor(
    val memoId:Long,
    val imageId:Long
)