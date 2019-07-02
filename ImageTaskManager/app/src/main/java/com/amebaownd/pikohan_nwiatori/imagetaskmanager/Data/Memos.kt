package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.sql.Time

@Entity
data class Memos constructor(
    @PrimaryKey(autoGenerate = true)
    val memoId : Long,
    val title :String,
    val created: Long,
    val memo :String
)