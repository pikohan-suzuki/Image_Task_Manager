package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.sql.Time

@Entity
data class Memos constructor(
    @PrimaryKey(autoGenerate = true)
    var memoId : Long=0,
    var title :String="",
    var created: Long=0,
    var memo :String=""
)